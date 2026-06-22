package com.healthcare.radiology_manager.repository;

import com.healthcare.radiology_manager.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    boolean existsBySerialNumber(String serialNumber);
    
    List<Equipment> findAllByOrganizationId(Long organizationId);
}
