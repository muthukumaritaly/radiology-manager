package com.healthcare.radiology_manager.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "equipment", indexes = {
    @Index(name = "idx_equipment_org", columnList = "organization_id"),
    @Index(name = "idx_equipment_container", columnList = "container_id"),
    @Index(name = "idx_equipment_serial", columnList = "serial_number", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id")
    private Container container;

    public Equipment(String name, String type, String serialNumber, LocalDate installationDate, Organization organization) {
        this.name = name;
        this.type = type;
        this.serialNumber = serialNumber;
        this.installationDate = installationDate;
        this.organization = organization;
    }

    public Equipment(String name, String type, String serialNumber, LocalDate installationDate, Organization organization, Container container) {
        this.name = name;
        this.type = type;
        this.serialNumber = serialNumber;
        this.installationDate = installationDate;
        this.organization = organization;
        this.container = container;
    }
}
