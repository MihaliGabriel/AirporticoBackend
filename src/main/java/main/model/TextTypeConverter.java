package main.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TextTypeConverter implements AttributeConverter<TextType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TextType textType) {
        if (textType == null) {
            return null;
        }
        return textType.getValue();
    }

    @Override
    public TextType convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        for (TextType textType : TextType.values()) {
            if (textType.getValue() == value) {
                return textType;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
