package com.healthcare.radiology_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentCreateRequest {
    @NotBlank(message = "Equipment name must not be blank")
    private String name;

    @NotBlank(message = "Equipment type must not be blank")
    private String type;

    @NotBlank(message = "Serial number must not be blank")
    private String serialNumber;

    @NotNull(message = "Installation date must not be null")
    @PastOrPresent(message = "Installation date must be in the past or present")
    private LocalDate installationDate;

    @NotNull(message = "Organization ID must not be null")
    private Long organizationId;

    private Long containerId;
}
