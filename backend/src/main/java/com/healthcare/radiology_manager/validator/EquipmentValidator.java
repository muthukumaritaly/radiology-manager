package com.healthcare.radiology_manager.validator;

import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Organization;

public interface EquipmentValidator {
    void validateCreateRequest(EquipmentCreateRequest request, Organization organization, Container container);
}
