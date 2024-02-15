package main.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TicketStatusConverter implements AttributeConverter<TicketStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TicketStatus ticketStatus) {
        if (ticketStatus == null) {
            return null;
        }
        return ticketStatus.getValue();
    }

    @Override
    public TicketStatus convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        for (TicketStatus ticketStatus : TicketStatus.values()) {
            if (ticketStatus.getValue() == value) {
                return ticketStatus;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
