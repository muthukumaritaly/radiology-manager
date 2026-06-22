package com.healthcare.radiology_manager.validator;

import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.exception.ValidationException;
import com.healthcare.radiology_manager.repository.EquipmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EquipmentValidatorImpl implements EquipmentValidator {

    private final EquipmentRepository equipmentRepository;

    public EquipmentValidatorImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public void validateCreateRequest(EquipmentCreateRequest request, Organization organization, Container container) {
        if (container != null) {
            if (!container.getOrganization().getId().equals(organization.getId())) {
                log.error("Container validation failed: Container ID {} belongs to Org {}, expected Org {}", 
                          request.getContainerId(), container.getOrganization().getId(), organization.getId());
                throw new ValidationException(
                    "Container (ID: " + request.getContainerId() + " - Org: " + container.getOrganization().getId() + 
                    ") does not belong to the target Organization (ID: " + organization.getId() + ")"
                );
            }
        }

        if (equipmentRepository.existsBySerialNumber(request.getSerialNumber())) {
            log.warn("Pre-flight validation failed: Equipment with Serial Number '{}' already exists", request.getSerialNumber());
            throw new ValidationException("Equipment with Serial Number '" + request.getSerialNumber() + "' already exists");
        }
    }
}
