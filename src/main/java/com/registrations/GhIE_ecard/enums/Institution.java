package com.registrations.GhIE_ecard.enums;

public enum Institution {
    // University Enums
    KNUST("KNUST"),
    UG("University of Ghana"),
    UMAT("UMaT"),
    UENR("UENR"),
    ASHESI("Ashesi University"),
    ANU("All Nations University"),
    CENTRAL_U("Central University"),
    GIMPA("GIMPA"),
    UCC("University of Cape Coast"),
    UDS("University for Development Studies"),
    CKTUTAS("CKT-UTAS"),
    VVU("Valley View University"),
    MUCG("Methodist University College"),
    CSUC("Christian Service University Co."),
    RMU("Regional Maritime University"),
    AAMUSTED("AAMUSTED"),


    // Technical University Enums
    TTU_TAKORADI("Takoradi Technical University"), // Renamed for clarity as there were two TTUs
    ATU("Accra Technical University"),
    KTU("Koforidua Technical University"),
    HTU("Ho Technical University"),
    CCTU("Cape Coast Technical University"),
    STU("Sunyani Technical University"),
    TaTU("Tamale Technical University"), // Renamed for clarity
    KsTU("Kumasi Technical University"),

    // Other Higher Schools Enums

    GCTU("GCTU"),
    REGENT("Regent University College"),
    WIUC("Wisconsin International University College"),
    PUC("Pentecost University");

    private final String displayName;

    // Constructor for Institution
    Institution(String displayName) {
        this.displayName = displayName;

    }

    // getter method to get names
    public String getDisplayName() {
        return this.displayName;
    }

    // Optional: Override toString() for default behavior
    @Override
    public String toString() {
        return this.displayName;
    }


}
