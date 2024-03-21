package main.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AuthenticationMethodConverter implements AttributeConverter<AuthenticationMethod, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AuthenticationMethod authenticationMethod) {
        if (authenticationMethod == null) {
            return null;
        }
        return authenticationMethod.getValue();
    }

    @Override
    public AuthenticationMethod convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        for (AuthenticationMethod authenticationMethod : AuthenticationMethod.values()) {
            if (authenticationMethod.getValue() == value) {
                return authenticationMethod;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
