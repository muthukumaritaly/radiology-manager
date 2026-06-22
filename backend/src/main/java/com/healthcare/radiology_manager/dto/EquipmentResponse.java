package com.healthcare.radiology_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentResponse {
    private Long id;
    private String name;
    private String type;
    private String serialNumber;
    private LocalDate installationDate;
    private Long organizationId;
    private Long containerId;
}
