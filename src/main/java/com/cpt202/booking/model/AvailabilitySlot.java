package com.cpt202.booking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean booked;

    public AvailabilitySlot() {
    }

    public Long getId() {
        return id;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
