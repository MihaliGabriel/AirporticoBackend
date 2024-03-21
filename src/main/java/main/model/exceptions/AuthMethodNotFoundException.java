package main.model.exceptions;

public class AuthMethodNotFoundException extends Exception{
    public AuthMethodNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
