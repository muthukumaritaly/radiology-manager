package com.healthcare.radiology_manager.service;

import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;


public interface OrganizationService {
    OrganizationTreeResponse getOrganizationTree(Long organizationId);
}
