package com.healthcare.radiology_manager.service;

import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.dto.EquipmentResponse;


public interface EquipmentService {

    EquipmentResponse createEquipment(EquipmentCreateRequest request);
}
