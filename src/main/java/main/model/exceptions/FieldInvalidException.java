package main.model.exceptions;

public class FieldInvalidException extends Exception{
    public FieldInvalidException(String errorMessage) {
        super(errorMessage);
    }
}
