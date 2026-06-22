package com.healthcare.radiology_manager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationTreeResponse {
    private Long id;
    private String name;
    private List<EquipmentResponse> equipment = new ArrayList<>();
    private List<ContainerTreeNode> containers = new ArrayList<>();

    public OrganizationTreeResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
