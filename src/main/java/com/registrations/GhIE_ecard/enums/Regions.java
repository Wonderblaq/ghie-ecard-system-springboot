package com.registrations.GhIE_ecard.enums;

public enum Regions{
    // Region enums
    Ahafo("Ahafo Region"),
    Ashanti("Ashanti Region"),
    Bono("Bono Region"),
    BonoEast("Bono East Region"),
    Central("Central Region"),
    Eastern("Eastern Region"),
    GA("Greater Accra Region"),
    NorthEast(" North East Region"),
    Northern(" Northern Region"),
    Oti("Oti Region"),
    Savannah("Savannah Region"),
    UpperEast("Upper East"),
    UpperWest("Upper West"),
    Volta("Volta Region"),
    Western("Western Region"),
    WesternNorth("Western North");


    private final String regionName;
    Regions(String regionName){
        this.regionName = regionName;
    }


    public String getRegionName() {
        return this.regionName;
    }


}
