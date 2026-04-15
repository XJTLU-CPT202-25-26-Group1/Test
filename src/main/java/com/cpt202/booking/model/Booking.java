package com.cpt202.booking.model;

import com.cpt202.booking.enums.BookingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;
    private String topic;
    private String notes;
    private String rejectionReason;
    private Double calculatedFee;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @ManyToOne
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private AvailabilitySlot slot;

    public Booking() {
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
    }

    public Booking(String customerName, String customerEmail, String topic, String notes, BookingStatus status, Specialist specialist, AvailabilitySlot slot) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.topic = topic;
        this.notes = notes;
        this.status = status;
        this.specialist = specialist;
        this.slot = slot;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getTopic() {
        return topic;
    }

    public String getNotes() {
        return notes;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Double getCalculatedFee() {
        return calculatedFee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public AvailabilitySlot getSlot() {
        return slot;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setCalculatedFee(Double calculatedFee) {
        this.calculatedFee = calculatedFee;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public void setSlot(AvailabilitySlot slot) {
        this.slot = slot;
    }
}
