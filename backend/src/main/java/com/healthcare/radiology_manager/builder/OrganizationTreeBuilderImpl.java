package com.healthcare.radiology_manager.builder;

import com.healthcare.radiology_manager.dto.ContainerTreeNode;
import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.mapper.EquipmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrganizationTreeBuilderImpl implements OrganizationTreeBuilder {

    private final EquipmentMapper equipmentMapper;

    public OrganizationTreeBuilderImpl(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    public OrganizationTreeResponse buildTree(Organization organization, List<Container> allContainers, List<Equipment> allEquipment) {
        long startTime = System.nanoTime();
        log.debug("Assembling organization tree in-memory for Org ID: {}. Containers count: {}, Equipment count: {}", 
                  organization.getId(), allContainers.size(), allEquipment.size());

        Map<Long, ContainerTreeNode> nodeMap = allContainers.stream()
                .collect(Collectors.toMap(
                        Container::getId,
                        c -> new ContainerTreeNode(c.getId(), c.getName())
                ));

        Map<Long, List<ContainerTreeNode>> parentToChildrenMap = new HashMap<>();
        List<ContainerTreeNode> rootContainers = new ArrayList<>();

        for (Container container : allContainers) {
            ContainerTreeNode node = nodeMap.get(container.getId());
            if (container.getParentContainer() == null) {
                rootContainers.add(node);
            } else {
                Long parentId = container.getParentContainer().getId();
                parentToChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
            }
        }

        List<EquipmentResponse> directEquipment = new ArrayList<>();
        Map<Long, List<EquipmentResponse>> containerToEquipmentMap = new HashMap<>();

        for (Equipment eq : allEquipment) {
            EquipmentResponse response = equipmentMapper.toResponse(eq);
            if (eq.getContainer() == null) {
                directEquipment.add(response);
            } else {
                Long containerId = eq.getContainer().getId();
                containerToEquipmentMap.computeIfAbsent(containerId, k -> new ArrayList<>()).add(response);
            }
        }

        Set<Long> visited = new HashSet<>();
        for (ContainerTreeNode rootNode : rootContainers) {
            log.trace("Starting recursive assembly from root node ID: {}", rootNode.getId());
            assembleSubtree(rootNode, parentToChildrenMap, containerToEquipmentMap, visited);
        }

        OrganizationTreeResponse treeResponse = new OrganizationTreeResponse(organization.getId(), organization.getName());
        treeResponse.setEquipment(directEquipment);
        treeResponse.setContainers(rootContainers);

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        log.info("Organization tree built successfully for Org ID: {} in {} ms. Root Containers: {}, Direct Equipment: {}", 
                 organization.getId(), String.format("%.3f", durationMs), rootContainers.size(), directEquipment.size());

        return treeResponse;
    }

    private void assembleSubtree(
            ContainerTreeNode node,
            Map<Long, List<ContainerTreeNode>> parentToChildrenMap,
            Map<Long, List<EquipmentResponse>> containerToEquipmentMap,
            Set<Long> visited) {
        
        if (visited.contains(node.getId())) {
            log.error("Cycle detected in container relationships at node ID: {}. Halting recursion.", node.getId());
            return;
        }
        visited.add(node.getId());

        List<EquipmentResponse> eqList = containerToEquipmentMap.getOrDefault(node.getId(), Collections.emptyList());
        log.trace("Container ID {} has {} directly assigned equipment", node.getId(), eqList.size());
        node.setEquipment(new ArrayList<>(eqList));

        List<ContainerTreeNode> children = parentToChildrenMap.getOrDefault(node.getId(), Collections.emptyList());
        log.trace("Container ID {} has {} sub-containers", node.getId(), children.size());
        for (ContainerTreeNode child : children) {
            node.addSubContainer(child);
            assembleSubtree(child, parentToChildrenMap, containerToEquipmentMap, visited);
        }
    }
}
