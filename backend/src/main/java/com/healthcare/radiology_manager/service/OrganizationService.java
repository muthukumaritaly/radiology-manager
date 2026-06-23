package com.healthcare.radiology_manager.service;

import com.healthcare.radiology_manager.dto.OrganizationResponse;
import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import java.util.List;

public interface OrganizationService {
    OrganizationTreeResponse getOrganizationTree(Long organizationId);
    List<OrganizationResponse> getAllOrganizations();
}
