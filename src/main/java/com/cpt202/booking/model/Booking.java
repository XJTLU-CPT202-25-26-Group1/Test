package com.cpt202.booking.model;

import jakarta.persistence.*;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String specialistName;
    private String bookingDate;
    private String bookingTime;
    private String topic;
    private String status;

    public Booking() {
    }

    public Booking(String customerName, String specialistName, String bookingDate, String bookingTime, String topic, String status) {
        this.customerName = customerName;
        this.specialistName = specialistName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.topic = topic;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getSpecialistName() {
        return specialistName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public String getTopic() {
        return topic;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setSpecialistName(String specialistName) {
        this.specialistName = specialistName;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}