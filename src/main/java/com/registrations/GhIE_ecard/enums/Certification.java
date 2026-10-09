package com.registrations.GhIE_ecard.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Certification {
    HND("HND"),
    Btech("BTech"),
    Bsc("Bsc");


    private final String displayCertType;

    Certification(String displayCertType) {
        this.displayCertType = displayCertType;
    }
    @JsonValue
    public String getDisplayCertType(){
        return this.displayCertType;
    }

    @JsonCreator
    public static Certification fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (Certification cert : Certification.values()) {
            if (cert.name().equalsIgnoreCase(text.trim()) ||
                    cert.displayCertType.equalsIgnoreCase(text.trim())) {
                return cert;
            }
        }
        throw new IllegalArgumentException("Unknown certificate: " + text);
    }

}
