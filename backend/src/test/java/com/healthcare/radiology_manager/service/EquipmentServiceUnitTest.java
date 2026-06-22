package com.healthcare.radiology_manager.service;

import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.exception.ResourceNotFoundException;
import com.healthcare.radiology_manager.exception.ValidationException;
import com.healthcare.radiology_manager.mapper.EquipmentMapper;
import com.healthcare.radiology_manager.repository.ContainerRepository;
import com.healthcare.radiology_manager.repository.EquipmentRepository;
import com.healthcare.radiology_manager.repository.OrganizationRepository;
import com.healthcare.radiology_manager.validator.EquipmentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceUnitTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private ContainerRepository containerRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentValidator equipmentValidator;

    @Mock
    private EquipmentMapper equipmentMapper;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private Organization organization;
    private Container container;
    private Equipment equipment;
    private EquipmentCreateRequest validRequest;
    private EquipmentResponse expectedResponse;

    @BeforeEach
    void setUp() {
        organization = new Organization("General Hospital");
        organization.setId(1L);

        container = new Container("Radiology Wing", organization, null);
        container.setId(10L);

        equipment = new Equipment(
                "MRI Machine",
                "MRI",
                "SN12345",
                LocalDate.of(2026, 6, 19),
                organization,
                container
        );
        equipment.setId(100L);

        validRequest = new EquipmentCreateRequest(
                "MRI Machine",
                "MRI",
                "SN12345",
                LocalDate.of(2026, 6, 19),
                1L,
                10L
        );

        expectedResponse = new EquipmentResponse(
                100L,
                "MRI Machine",
                "MRI",
                "SN12345",
                LocalDate.of(2026, 6, 19),
                1L,
                10L
        );
    }

    @Test
    @DisplayName("Create Equipment - Success with Container")
    void createEquipment_successfulWithContainer() {
        // Arrange
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(containerRepository.findById(10L)).thenReturn(Optional.of(container));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);
        when(equipmentMapper.toResponse(any(Equipment.class))).thenReturn(expectedResponse);

        // Act
        EquipmentResponse response = equipmentService.createEquipment(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getName()).isEqualTo("MRI Machine");
        assertThat(response.getType()).isEqualTo("MRI");
        assertThat(response.getSerialNumber()).isEqualTo("SN12345");
        assertThat(response.getOrganizationId()).isEqualTo(1L);
        assertThat(response.getContainerId()).isEqualTo(10L);

        verify(organizationRepository).findById(1L);
        verify(containerRepository).findById(10L);
        verify(equipmentValidator).validateCreateRequest(validRequest, organization, container);
        
        ArgumentCaptor<Equipment> captor = ArgumentCaptor.forClass(Equipment.class);
        verify(equipmentRepository).save(captor.capture());
        Equipment saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("MRI Machine");
        assertThat(saved.getOrganization()).isEqualTo(organization);
        assertThat(saved.getContainer()).isEqualTo(container);
    }

    @Test
    @DisplayName("Create Equipment - Success without Container")
    void createEquipment_successfulWithoutContainer() {
        // Arrange
        EquipmentCreateRequest requestWithoutContainer = new EquipmentCreateRequest(
                "MRI Machine",
                "MRI",
                "SN12345",
                LocalDate.of(2026, 6, 19),
                1L,
                null
        );

        Equipment equipmentWithoutContainer = new Equipment(
                "MRI Machine",
                "MRI",
                "SN12345",
                LocalDate.of(2026, 6, 19),
                organization,
                null
        );
        equipmentWithoutContainer.setId(101L);

        EquipmentResponse responseWithoutContainer = new EquipmentResponse(
                101L,
                "MRI Machine",
                "MRI",
                "SN12345",
                LocalDate.of(2026, 6, 19),
                1L,
                null
        );

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipmentWithoutContainer);
        when(equipmentMapper.toResponse(any(Equipment.class))).thenReturn(responseWithoutContainer);

        // Act
        EquipmentResponse response = equipmentService.createEquipment(requestWithoutContainer);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getContainerId()).isNull();

        verify(organizationRepository).findById(1L);
        verify(containerRepository, never()).findById(anyLong());
        verify(equipmentValidator).validateCreateRequest(requestWithoutContainer, organization, null);
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Create Equipment - Throws ResourceNotFoundException when Organization not found")
    void createEquipment_throwsResourceNotFoundException_whenOrganizationNotFound() {
        // Arrange
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> equipmentService.createEquipment(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Organization not found with ID: 1");

        verify(organizationRepository).findById(1L);
        verifyNoInteractions(containerRepository, equipmentValidator, equipmentRepository);
    }

    @Test
    @DisplayName("Create Equipment - Throws ResourceNotFoundException when Container not found")
    void createEquipment_throwsResourceNotFoundException_whenContainerNotFound() {
        // Arrange
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(containerRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> equipmentService.createEquipment(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Container not found with ID: 10");

        verify(organizationRepository).findById(1L);
        verify(containerRepository).findById(10L);
        verifyNoInteractions(equipmentValidator, equipmentRepository);
    }
}
