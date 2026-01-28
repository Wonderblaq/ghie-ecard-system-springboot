package com.registrations.GhIE_ecard.enums;


import com.fasterxml.jackson.annotation.JsonValue;

public enum EnrollmentYear {
    Year_2021(2021),
    Year_2022(2022),
    Year_2023(2023),
    Year_2024(2024),
    Year_2025(2025),
    Year_2026(2026);


    public final Integer enrollmentYear;

    EnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    @JsonValue
    public Integer getEnrollmentYear() {
        return this.enrollmentYear;
    }
}
