package main.model;

public enum AirplaneSeatType {

    //OCCUPIED(TICKET ALREADY BOUGHT)
    OCCUPIED(1),
    //RESERVED(TICKET RESERVATION OR BUY PENDING)
    RESERVED(2),
    //UNOCCUPIED(PLANE SEAT IS FREE)
    UNOCCUPIED(3);
    private final int value;

    private AirplaneSeatType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
