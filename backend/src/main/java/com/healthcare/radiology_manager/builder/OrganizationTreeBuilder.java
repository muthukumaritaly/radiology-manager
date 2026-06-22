package com.healthcare.radiology_manager.builder;

import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import com.healthcare.radiology_manager.entity.Container;
import com.healthcare.radiology_manager.entity.Equipment;
import com.healthcare.radiology_manager.entity.Organization;

import java.util.List;

public interface OrganizationTreeBuilder {
    OrganizationTreeResponse buildTree(Organization organization, List<Container> containers, List<Equipment> equipment);
}
