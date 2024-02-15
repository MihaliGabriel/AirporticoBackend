package main.model;

public enum TextType {
    //AIRPLANE TEXT
    AIRPLANE_NAME(1001),
    //AIRPORT TEXT
    AIRPORT_NAME(2001),
    //COMPANY TEXT
    COMPANY_NAME(3001),
    //FLIGHT TEXT
    FLIGHT_NAME(4001),
    //LOCATION TEXT
    LOCATION_CITY(5001),
    LOCATION_COUNTRY(5002);

    private final int value;

    private TextType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
