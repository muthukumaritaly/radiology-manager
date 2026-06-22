package com.healthcare.radiology_manager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContainerTreeNode {
    private Long id;
    private String name;
    private List<ContainerTreeNode> containers = new ArrayList<>();
    private List<EquipmentResponse> equipment = new ArrayList<>();

    public ContainerTreeNode(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addSubContainer(ContainerTreeNode node) {
        this.containers.add(node);
    }
}
