package com.cpt202.booking.model;

import com.cpt202.booking.enums.RoleType;

public class User {

    private String username;
    private String password;
    private String displayName;
    private String email;
    private String phone;
    private RoleType role;
    private Long specialistId;
    private String resetToken;

    public User() {
    }

    public User(String username, String password, String displayName, String email, String phone, RoleType role) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public RoleType getRole() {
        return role;
    }

    public Long getSpecialistId() {
        return specialistId;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public void setSpecialistId(Long specialistId) {
        this.specialistId = specialistId;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
