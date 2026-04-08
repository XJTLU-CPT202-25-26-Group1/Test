package com.cpt202.booking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String expertise;
    private String availability;

    public Specialist() {
    }

    public Specialist(String name, String expertise, String availability) {
        this.name = name;
        this.expertise = expertise;
        this.availability = availability;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExpertise() {
        return expertise;
    }

    public String getAvailability() {
        return availability;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}