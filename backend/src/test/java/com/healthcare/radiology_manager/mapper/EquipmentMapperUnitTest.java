package com.healthcare.radiology_manager.mapper;

import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EquipmentMapperUnitTest {

    private final EquipmentMapper equipmentMapper = new EquipmentMapperImpl();

    @Test
    @DisplayName("Mapper: null input maps to null")
    void toResponse_nullInput() {
        assertThat(equipmentMapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("Mapper: maps equipment successfully with container")
    void toResponse_withContainer() {
        Organization org = new Organization("Test Org");
        org.setId(5L);
        Container container = new Container("Test Container", org);
        container.setId(10L);
        Equipment eq = new Equipment("MRI", "MRI", "SN-1", LocalDate.now(), org, container);
        eq.setId(100L);

        EquipmentResponse response = equipmentMapper.toResponse(eq);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getName()).isEqualTo("MRI");
        assertThat(response.getType()).isEqualTo("MRI");
        assertThat(response.getSerialNumber()).isEqualTo("SN-1");
        assertThat(response.getOrganizationId()).isEqualTo(5L);
        assertThat(response.getContainerId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Mapper: maps equipment successfully without container")
    void toResponse_withoutContainer() {
        Organization org = new Organization("Test Org");
        org.setId(5L);
        Equipment eq = new Equipment("X-Ray", "XR", "SN-2", LocalDate.now(), org, null);
        eq.setId(101L);

        EquipmentResponse response = equipmentMapper.toResponse(eq);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getContainerId()).isNull();
    }
}
