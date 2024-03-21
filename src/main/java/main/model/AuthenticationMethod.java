package main.model;

public enum AuthenticationMethod {
    LOCAL(0),
    GOOGLE(1),
    FACEBOOK(2);


    private final int value;

    private AuthenticationMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    }
