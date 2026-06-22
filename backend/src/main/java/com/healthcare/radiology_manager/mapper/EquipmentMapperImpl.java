package com.healthcare.radiology_manager.mapper;

import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.entity.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapperImpl implements EquipmentMapper {

    @Override
    public EquipmentResponse toResponse(Equipment eq) {
        if (eq == null) {
            return null;
        }
        return new EquipmentResponse(
                eq.getId(),
                eq.getName(),
                eq.getType(),
                eq.getSerialNumber(),
                eq.getInstallationDate(),
                eq.getOrganization().getId(),
                eq.getContainer() != null ? eq.getContainer().getId() : null
        );
    }
}
