package main.model;

public enum TicketStatus {

    //ticket that was bought
    BOUGHT(0),

    //ticket that was reserved for a period of time
    RESERVED(1);

    private final int value;

    private TicketStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
