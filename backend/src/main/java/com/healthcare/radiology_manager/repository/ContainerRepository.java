package com.healthcare.radiology_manager.repository;

import com.healthcare.radiology_manager.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
    List<Container> findAllByOrganizationId(Long organizationId);
}
