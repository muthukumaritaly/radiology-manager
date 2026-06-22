package com.healthcare.radiology_manager.service;

import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.exception.ValidationException;
import com.healthcare.radiology_manager.repository.ContainerRepository;
import com.healthcare.radiology_manager.repository.EquipmentRepository;
import com.healthcare.radiology_manager.repository.OrganizationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@Transactional
class RadiologyServicesIntegrationTest {

    @org.testcontainers.junit.jupiter.Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EntityManager entityManager;

    private Organization org1;
    private Container rootContainer;
    private Container subContainer;

    @BeforeEach
    void setUp() {
        // Reset sequences to prevent key conflicts with seeded data in PostgreSQL
        entityManager.createNativeQuery("SELECT setval('equipment_id_seq', COALESCE((SELECT MAX(id) FROM equipment), 0) + 1, false)").getSingleResult();
        entityManager.createNativeQuery("SELECT setval('organizations_id_seq', COALESCE((SELECT MAX(id) FROM organizations), 0) + 1, false)").getSingleResult();
        entityManager.createNativeQuery("SELECT setval('containers_id_seq', COALESCE((SELECT MAX(id) FROM containers), 0) + 1, false)").getSingleResult();

        // Fetch seeded entities from Liquibase migrations
        org1 = organizationRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Seeded Organization not found"));
        rootContainer = containerRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Seeded root Container not found"));
        subContainer = containerRepository.findById(2L)
                .orElseThrow(() -> new IllegalStateException("Seeded sub Container not found"));
    }

    @Test
    @DisplayName("Integration: Create Equipment - Successful persistence")
    void createEquipment_persistsSuccessfully() {
        // Arrange
        EquipmentCreateRequest request = new EquipmentCreateRequest(
                "New Integration X-Ray",
                "X-RAY",
                "INTEG-XR-999",
                LocalDate.now(),
                org1.getId(),
                subContainer.getId()
        );

        // Act
        EquipmentResponse response = equipmentService.createEquipment(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        
        // Verify in DB
        Equipment persisted = equipmentRepository.findById(response.getId()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted.getName()).isEqualTo("New Integration X-Ray");
        assertThat(persisted.getSerialNumber()).isEqualTo("INTEG-XR-999");
        assertThat(persisted.getOrganization().getId()).isEqualTo(org1.getId());
        assertThat(persisted.getContainer().getId()).isEqualTo(subContainer.getId());
    }

    @Test
    @DisplayName("Integration: Create Equipment - Throws validation error on duplicate serial number")
    void createEquipment_throwsException_onDuplicateSerialNumber() {
        // Arrange
        EquipmentCreateRequest request = new EquipmentCreateRequest(
                "New Integration X-Ray",
                "CT",
                "INTEG-XR-999",
                LocalDate.now(),
                1L,
                null
        );
        equipmentService.createEquipment(request);

        EquipmentCreateRequest duplicateRequest = new EquipmentCreateRequest(
                "Another Equipment",
                "CT",
                "INTEG-XR-999",
                LocalDate.now(),
                1L,
                null
        );

        // Act & Assert
        assertThatThrownBy(() -> equipmentService.createEquipment(duplicateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Integration: Get Organization Tree - Loads nested Containers and Equipment")
    void getOrganizationTree_returnsCompleteTree() {
        // Act
        OrganizationTreeResponse tree = organizationService.getOrganizationTree(1L);

        // Assert
        assertThat(tree).isNotNull();
        assertThat(tree.getId()).isEqualTo(org1.getId());
        assertThat(tree.getName()).isEqualTo("San Raffaele Hospital Group");

        // Verify global equipment is present
        assertThat(tree.getEquipment()).isNotEmpty();
        assertThat(tree.getEquipment()).anyMatch(eq -> eq.getSerialNumber().equals("SN-MG-404"));

        // Verify containers mapping is loaded recursively
        assertThat(tree.getContainers()).isNotEmpty();
        // Container with ID 1 (Plant A) should be a root container
        assertThat(tree.getContainers()).anyMatch(c -> c.getId().equals(1L) && c.getName().equals("Plant A"));
    }
}
