package com.healthcare.radiology_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.service.EquipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EquipmentController.class)
class EquipmentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("EquipmentController: Create equipment successfully")
    void createEquipment_success() throws Exception {
        EquipmentCreateRequest request = new EquipmentCreateRequest(
                "MRI Machine", "MRI", "SN12345", LocalDate.now(), 1L, 10L
        );
        EquipmentResponse response = new EquipmentResponse(
                100L, "MRI Machine", "MRI", "SN12345", LocalDate.now(), 1L, 10L
        );

        when(equipmentService.createEquipment(any(EquipmentCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/equipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.name").value("MRI Machine"))
                .andExpect(jsonPath("$.serialNumber").value("SN12345"));
    }
}
