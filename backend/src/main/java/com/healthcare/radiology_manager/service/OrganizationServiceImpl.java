package com.healthcare.radiology_manager.service;

import com.healthcare.radiology_manager.builder.OrganizationTreeBuilder;
import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.exception.ResourceNotFoundException;
import com.healthcare.radiology_manager.repository.ContainerRepository;
import com.healthcare.radiology_manager.repository.EquipmentRepository;
import com.healthcare.radiology_manager.repository.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ContainerRepository containerRepository;
    private final EquipmentRepository equipmentRepository;
    private final OrganizationTreeBuilder organizationTreeBuilder;

    public OrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            ContainerRepository containerRepository,
            EquipmentRepository equipmentRepository,
            OrganizationTreeBuilder organizationTreeBuilder) {
        this.organizationRepository = organizationRepository;
        this.containerRepository = containerRepository;
        this.equipmentRepository = equipmentRepository;
        this.organizationTreeBuilder = organizationTreeBuilder;
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationTreeResponse getOrganizationTree(Long organizationId) {
        log.info("Fetching complete hierarchy tree for Organization ID: {}", organizationId);

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization search failed for tree retrieval. ID: {}", organizationId);
                    return new ResourceNotFoundException("Organization not found with ID: " + organizationId);
                });

        List<Container> allContainers = containerRepository.findAllByOrganizationId(organizationId);
        List<Equipment> allEquipment = equipmentRepository.findAllByOrganizationId(organizationId);

        log.debug("Bulk loaded {} containers and {} equipment for Org ID: {}", 
                  allContainers.size(), allEquipment.size(), organizationId);

        return organizationTreeBuilder.buildTree(organization, allContainers, allEquipment);
    }
}
