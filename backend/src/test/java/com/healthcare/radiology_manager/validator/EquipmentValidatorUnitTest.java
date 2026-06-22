package com.healthcare.radiology_manager.validator;

import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Organization;
import com.healthcare.radiology_manager.exception.ValidationException;
import com.healthcare.radiology_manager.repository.EquipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentValidatorUnitTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentValidatorImpl equipmentValidator;

    private Organization organization;
    private Container validContainer;
    private EquipmentCreateRequest request;

    @BeforeEach
    void setUp() {
        organization = new Organization("Test Org");
        organization.setId(1L);

        validContainer = new Container("Valid Container", organization);
        validContainer.setId(10L);

        request = new EquipmentCreateRequest(
                "MRI",
                "MRI",
                "SN-123",
                LocalDate.now(),
                1L,
                10L
        );
    }

    @Test
    @DisplayName("Validator: validation succeeds for valid request")
    void validateCreateRequest_success() {
        when(equipmentRepository.existsBySerialNumber("SN-123")).thenReturn(false);

        assertThatCode(() -> equipmentValidator.validateCreateRequest(request, organization, validContainer))
                .doesNotThrowAnyException();

        verify(equipmentRepository).existsBySerialNumber("SN-123");
    }

    @Test
    @DisplayName("Validator: validation fails when container belongs to different organization")
    void validateCreateRequest_mismatchedOrganization() {
        Organization otherOrg = new Organization("Other Org");
        otherOrg.setId(99L);
        Container mismatchedContainer = new Container("Mismatched Container", otherOrg);
        mismatchedContainer.setId(10L);

        assertThatThrownBy(() -> equipmentValidator.validateCreateRequest(request, organization, mismatchedContainer))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("does not belong to the target Organization");

        verifyNoInteractions(equipmentRepository);
    }

    @Test
    @DisplayName("Validator: validation fails when serial number already exists")
    void validateCreateRequest_duplicateSerialNumber() {
        when(equipmentRepository.existsBySerialNumber("SN-123")).thenReturn(true);

        assertThatThrownBy(() -> equipmentValidator.validateCreateRequest(request, organization, validContainer))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already exists");

        verify(equipmentRepository).existsBySerialNumber("SN-123");
    }
}
