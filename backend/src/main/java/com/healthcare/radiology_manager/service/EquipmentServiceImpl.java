package com.healthcare.radiology_manager.service;

import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.exception.ResourceNotFoundException;
import com.healthcare.radiology_manager.mapper.EquipmentMapper;
import com.healthcare.radiology_manager.repository.ContainerRepository;
import com.healthcare.radiology_manager.repository.EquipmentRepository;
import com.healthcare.radiology_manager.repository.OrganizationRepository;
import com.healthcare.radiology_manager.validator.EquipmentValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {

    private final OrganizationRepository organizationRepository;
    private final ContainerRepository containerRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentValidator equipmentValidator;
    private final EquipmentMapper equipmentMapper;

    public EquipmentServiceImpl(
            OrganizationRepository organizationRepository,
            ContainerRepository containerRepository,
            EquipmentRepository equipmentRepository,
            EquipmentValidator equipmentValidator,
            EquipmentMapper equipmentMapper) {
        this.organizationRepository = organizationRepository;
        this.containerRepository = containerRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentValidator = equipmentValidator;
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    @Transactional
    public EquipmentResponse createEquipment(EquipmentCreateRequest request) {
        log.info("Request to create equipment. Name: '{}', Serial: '{}', OrgId: {}", 
                 request.getName(), request.getSerialNumber(), request.getOrganizationId());

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization search failed for ID: {}", request.getOrganizationId());
                    return new ResourceNotFoundException("Organization not found with ID: " + request.getOrganizationId());
                });

        Container container = null;
        if (request.getContainerId() != null) {
            container = containerRepository.findById(request.getContainerId())
                    .orElseThrow(() -> {
                        log.warn("Container search failed for ID: {}", request.getContainerId());
                        return new ResourceNotFoundException("Container not found with ID: " + request.getContainerId());
                    });
        }

        equipmentValidator.validateCreateRequest(request, organization, container);

        Equipment equipment = new Equipment(
                request.getName(),
                request.getType(),
                request.getSerialNumber(),
                request.getInstallationDate(),
                organization,
                container
        );

        Equipment savedEquipment = equipmentRepository.save(equipment);
        log.info("Successfully registered equipment. ID: {}, Serial: '{}'", savedEquipment.getId(), savedEquipment.getSerialNumber());

        return equipmentMapper.toResponse(savedEquipment);
    }
}
