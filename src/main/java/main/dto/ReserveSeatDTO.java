package main.dto;

import java.util.List;

public class ReserveSeatDTO {
    private String buyerSeat;
    private List<PassengerDTO> passengers;
    private FlightDTO flightDTO;

    public String getBuyerSeat() {
        return buyerSeat;
    }

    public void setBuyerSeat(String buyerSeat) {
        this.buyerSeat = buyerSeat;
    }

    public List<PassengerDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerDTO> passengers) {
        this.passengers = passengers;
    }

    public FlightDTO getFlightDTO() {
        return flightDTO;
    }

    public void setFlightDTO(FlightDTO flightDTO) {
        this.flightDTO = flightDTO;
    }

    @Override
    public String toString() {
        return "ReserveSeatDTO{" +
                "buyerSeat='" + buyerSeat + '\'' +
                ", passengers=" + passengers +
                ", flightDTO=" + flightDTO +
                '}';
    }
}
