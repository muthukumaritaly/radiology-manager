package com.healthcare.radiology_manager.builder;

import com.healthcare.radiology_manager.dto.ContainerTreeNode;
import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.mapper.EquipmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationTreeBuilderUnitTest {

    @Mock
    private EquipmentMapper equipmentMapper;

    @InjectMocks
    private OrganizationTreeBuilderImpl treeBuilder;

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization("General Hospital");
        organization.setId(1L);
    }

    @Test
    @DisplayName("TreeBuilder: builds tree successfully")
    void buildTree_success() {
        Container rootContainer = new Container("Building A", organization, null);
        rootContainer.setId(10L);
        Container childContainer = new Container("Floor 1", organization, rootContainer);
        childContainer.setId(20L);

        Equipment eqDirect = new Equipment("Global Ultrasound", "US", "SN999", LocalDate.now(), organization, null);
        eqDirect.setId(200L);
        Equipment eqInChild = new Equipment("X-Ray Machine", "XR", "SN888", LocalDate.now(), organization, childContainer);
        eqInChild.setId(300L);

        List<Container> allContainers = Arrays.asList(rootContainer, childContainer);
        List<Equipment> allEquipment = Arrays.asList(eqDirect, eqInChild);

        EquipmentResponse directRes = new EquipmentResponse(200L, "Global Ultrasound", "US", "SN999", LocalDate.now(), 1L, null);
        EquipmentResponse childRes = new EquipmentResponse(300L, "X-Ray Machine", "XR", "SN888", LocalDate.now(), 1L, 20L);

        when(equipmentMapper.toResponse(eqDirect)).thenReturn(directRes);
        when(equipmentMapper.toResponse(eqInChild)).thenReturn(childRes);

        // Act
        OrganizationTreeResponse response = treeBuilder.buildTree(organization, allContainers, allEquipment);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("General Hospital");

        // Assert direct equipment
        assertThat(response.getEquipment()).hasSize(1);
        assertThat(response.getEquipment().get(0).getId()).isEqualTo(200L);

        // Assert containers hierarchy
        assertThat(response.getContainers()).hasSize(1);
        ContainerTreeNode rootNode = response.getContainers().get(0);
        assertThat(rootNode.getId()).isEqualTo(10L);
        assertThat(rootNode.getName()).isEqualTo("Building A");

        assertThat(rootNode.getContainers()).hasSize(1);
        ContainerTreeNode childNode = rootNode.getContainers().get(0);
        assertThat(childNode.getId()).isEqualTo(20L);
        assertThat(childNode.getEquipment()).hasSize(1);
        assertThat(childNode.getEquipment().get(0).getId()).isEqualTo(300L);
    }

    @Test
    @DisplayName("TreeBuilder: halts recursion when cycle is detected")
    void buildTree_haltsRecursion_whenCycleDetected() {
        Container c1 = mock(Container.class);
        when(c1.getId()).thenReturn(10L);
        when(c1.getName()).thenReturn("C1");
        when(c1.getParentContainer()).thenReturn(null);

        Container c2 = mock(Container.class);
        when(c2.getId()).thenReturn(20L);
        when(c2.getName()).thenReturn("C2");
        when(c2.getParentContainer()).thenReturn(c1);

        Container c3 = mock(Container.class);
        when(c3.getId()).thenReturn(30L);
        when(c3.getName()).thenReturn("C3");
        when(c3.getParentContainer()).thenReturn(c2);

        Container c2Dup = mock(Container.class);
        when(c2Dup.getName()).thenReturn("C2-Dup");
        // To bypass duplicate keys in Collectors.toMap, return 200 first, then return 20 inside the loop
        when(c2Dup.getId()).thenReturn(200L, 200L, 20L);
        when(c2Dup.getParentContainer()).thenReturn(c3);

        List<Container> allContainers = Arrays.asList(c1, c2, c3, c2Dup);
        List<Equipment> allEquipment = Collections.emptyList();

        // Act
        OrganizationTreeResponse response = treeBuilder.buildTree(organization, allContainers, allEquipment);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getContainers()).hasSize(1);
        ContainerTreeNode root = response.getContainers().get(0);
        assertThat(root.getId()).isEqualTo(10L);
        assertThat(root.getContainers()).hasSize(1);
        
        ContainerTreeNode childNode = root.getContainers().get(0);
        assertThat(childNode.getId()).isEqualTo(20L);
        assertThat(childNode.getContainers()).hasSize(1);
        
        ContainerTreeNode grandchild = childNode.getContainers().get(0);
        assertThat(grandchild.getId()).isEqualTo(30L);
        assertThat(grandchild.getContainers()).hasSize(1);
        assertThat(grandchild.getContainers().get(0).getId()).isEqualTo(20L);
    }
}
