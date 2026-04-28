package com.cpt202.booking.enums;

public enum GenderType {
    MALE("Male"),
    FEMALE("Female"),
    UNSPECIFIED("Not Set");

    private final String label;

    GenderType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static GenderType[] selectableValues() {
        return new GenderType[]{MALE, FEMALE};
    }
}
