package main.dto;

import java.util.List;

public class TicketDTO {
    private Long id;
    private Long userId;
    private String ticketName;
    private String flightName;
    private PersonDTO person;
    private List<PassengerDTO> passengers;
    private Double price;
    private String ticketType;
    private String ticketStatus;

    public String getFlightName() {
        return flightName;
    }

    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }

    public List<PassengerDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerDTO> passengers) {
        this.passengers = passengers;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String flightName) {
        this.ticketName = this.getId() + "-" + flightName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    @Override
    public String toString() {
        return "TicketDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticketName='" + ticketName + '\'' +
                ", flightName='" + flightName + '\'' +
                ", person=" + person +
                ", passengers=" + passengers +
                ", price=" + price +
                ", ticketType='" + ticketType + '\'' +
                ", ticketStatus='" + ticketStatus + '\'' +
                '}';
    }
}
