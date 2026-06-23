package com.healthcare.radiology_manager.service;

import com.healthcare.radiology_manager.builder.OrganizationTreeBuilder;
import com.healthcare.radiology_manager.dto.OrganizationResponse;
import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.exception.ResourceNotFoundException;
import com.healthcare.radiology_manager.repository.ContainerRepository;
import com.healthcare.radiology_manager.repository.EquipmentRepository;
import com.healthcare.radiology_manager.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceUnitTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private ContainerRepository containerRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private OrganizationTreeBuilder organizationTreeBuilder;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization("General Hospital");
        organization.setId(1L);
    }

    @Test
    @DisplayName("Get Organization Tree - Success tree generation delegation")
    void getOrganizationTree_successful() {
        // Arrange
        Long orgId = 1L;
        List<Container> mockContainers = new ArrayList<>();
        List<Equipment> mockEquipment = new ArrayList<>();
        OrganizationTreeResponse expectedResponse = new OrganizationTreeResponse(orgId, "General Hospital");

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(organization));
        when(containerRepository.findAllByOrganizationId(orgId)).thenReturn(mockContainers);
        when(equipmentRepository.findAllByOrganizationId(orgId)).thenReturn(mockEquipment);
        when(organizationTreeBuilder.buildTree(organization, mockContainers, mockEquipment)).thenReturn(expectedResponse);

        // Act
        OrganizationTreeResponse response = organizationService.getOrganizationTree(orgId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(orgId);
        assertThat(response.getName()).isEqualTo("General Hospital");

        verify(organizationRepository).findById(orgId);
        verify(containerRepository).findAllByOrganizationId(orgId);
        verify(equipmentRepository).findAllByOrganizationId(orgId);
        verify(organizationTreeBuilder).buildTree(organization, mockContainers, mockEquipment);
    }

    @Test
    @DisplayName("Get Organization Tree - Throws ResourceNotFoundException when Organization not found")
    void getOrganizationTree_throwsResourceNotFoundException_whenOrganizationNotFound() {
        // Arrange
        Long orgId = 1L;
        when(organizationRepository.findById(orgId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> organizationService.getOrganizationTree(orgId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Organization not found with ID: 1");

        verify(organizationRepository).findById(orgId);
        verifyNoInteractions(containerRepository, equipmentRepository, organizationTreeBuilder);
    }

    @Test
    @DisplayName("Get All Organizations - Returns list of OrganizationResponse")
    void getAllOrganizations_successful() {
        // Arrange
        Organization org1 = new Organization("San Raffaele");
        org1.setId(1L);
        Organization org2 = new Organization("ASL 1");
        org2.setId(2L);
        when(organizationRepository.findAll()).thenReturn(List.of(org1, org2));

        // Act
        var responses = organizationService.getAllOrganizations();

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).name()).isEqualTo("San Raffaele");
        assertThat(responses.get(1).id()).isEqualTo(2L);
        assertThat(responses.get(1).name()).isEqualTo("ASL 1");

        verify(organizationRepository).findAll();
    }
}
