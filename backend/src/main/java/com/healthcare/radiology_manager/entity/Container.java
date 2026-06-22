package com.healthcare.radiology_manager.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "containers", indexes = {
    @Index(name = "idx_container_org", columnList = "organization_id"),
    @Index(name = "idx_container_parent", columnList = "parent_container_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_container_id")
    private Container parentContainer;

    @OneToMany(mappedBy = "parentContainer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Container> subContainers = new ArrayList<>();

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Equipment> equipment = new ArrayList<>();

    public Container(String name, Organization organization) {
        this.name = name;
        this.organization = organization;
    }

    public Container(String name, Organization organization, Container parentContainer) {
        this.name = name;
        this.organization = organization;
        this.parentContainer = parentContainer;
    }

}
