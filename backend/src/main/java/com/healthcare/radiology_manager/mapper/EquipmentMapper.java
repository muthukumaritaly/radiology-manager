package com.healthcare.radiology_manager.mapper;

import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.entity.Equipment;

public interface EquipmentMapper {
    EquipmentResponse toResponse(Equipment equipment);
}
