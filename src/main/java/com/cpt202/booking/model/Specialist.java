package com.cpt202.booking.model;

import com.cpt202.booking.enums.SpecialistStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String level;
    private Double feeRate;
    private String profileDescription;

    @Enumerated(EnumType.STRING)
    private SpecialistStatus status = SpecialistStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ExpertiseCategory category;

    public Specialist() {
    }

    public Specialist(String name, String level, Double feeRate, String profileDescription, SpecialistStatus status, ExpertiseCategory category) {
        this.name = name;
        this.level = level;
        this.feeRate = feeRate;
        this.profileDescription = profileDescription;
        this.status = status;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public Double getFeeRate() {
        return feeRate;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public SpecialistStatus getStatus() {
        return status;
    }

    public ExpertiseCategory getCategory() {
        return category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setFeeRate(Double feeRate) {
        this.feeRate = feeRate;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public void setStatus(SpecialistStatus status) {
        this.status = status;
    }

    public void setCategory(ExpertiseCategory category) {
        this.category = category;
    }
}
