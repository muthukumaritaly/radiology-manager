package com.healthcare.radiology_manager.controller;

import com.healthcare.radiology_manager.dto.OrganizationResponse;
import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import com.healthcare.radiology_manager.service.OrganizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;

    @Test
    @DisplayName("OrganizationController: Get tree successfully")
    void getOrganizationTree_success() throws Exception {
        OrganizationTreeResponse response = new OrganizationTreeResponse(1L, "San Raffaele Hospital Group");

        when(organizationService.getOrganizationTree(1L)).thenReturn(response);

        mockMvc.perform(get("/api/organizations/1/tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("San Raffaele Hospital Group"));
    }

    @Test
    @DisplayName("OrganizationController: Get all organizations successfully")
    void getAllOrganizations_success() throws Exception {
        OrganizationResponse response = new OrganizationResponse(1L, "San Raffaele Hospital Group");

        when(organizationService.getAllOrganizations()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/organizations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("San Raffaele Hospital Group"));
    }
}
