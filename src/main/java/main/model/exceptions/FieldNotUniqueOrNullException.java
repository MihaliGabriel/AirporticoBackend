package main.model.exceptions;

public class FieldNotUniqueOrNullException extends Exception{
    public FieldNotUniqueOrNullException(String errorMessage) {
        super(errorMessage);
    }
}
