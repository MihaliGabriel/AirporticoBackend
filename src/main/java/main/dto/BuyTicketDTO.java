package main.dto;

import java.util.List;

public class BuyTicketDTO {
    private String flightName;
    private Long id;
    private Integer nrOfPassengers;

    private Long userId;
    private List<PassengerDTO> passengers;
    private String ticketType;
    private String voucherCode;

    private Integer buyerSmallLuggage;
    private Integer buyerMediumLuggage;
    private Integer buyerLargeLuggage;
    private String buyerSeat;

    public String getFlightName() {
        return flightName;
    }

    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }

    public Integer getNrOfPassengers() {
        return nrOfPassengers;
    }

    public void setNrOfPassengers(Integer nrOfPassengers) {
        this.nrOfPassengers = nrOfPassengers;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<PassengerDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerDTO> passengers) {
        this.passengers = passengers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BuyTicketDTO{" +
                "flightName='" + flightName + '\'' +
                ", id=" + id +
                ", nrOfPassengers=" + nrOfPassengers +
                ", userId=" + userId +
                ", passengers=" + passengers +
                ", ticketType='" + ticketType + '\'' +
                ", voucherCode='" + voucherCode + '\'' +
                ", buyerSmallLuggage=" + buyerSmallLuggage +
                ", buyerMediumLuggage=" + buyerMediumLuggage +
                ", buyerLargeLuggage=" + buyerLargeLuggage +
                ", buyerSeat='" + buyerSeat + '\'' +
                '}';
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Integer getBuyerSmallLuggage() {
        return buyerSmallLuggage;
    }

    public void setBuyerSmallLuggage(Integer buyerSmallLuggage) {
        this.buyerSmallLuggage = buyerSmallLuggage;
    }

    public Integer getBuyerMediumLuggage() {
        return buyerMediumLuggage;
    }

    public void setBuyerMediumLuggage(Integer buyerMediumLuggage) {
        this.buyerMediumLuggage = buyerMediumLuggage;
    }

    public Integer getBuyerLargeLuggage() {
        return buyerLargeLuggage;
    }

    public void setBuyerLargeLuggage(Integer buyerLargeLuggage) {
        this.buyerLargeLuggage = buyerLargeLuggage;
    }

    public String getBuyerSeat() {
        return buyerSeat;
    }

    public void setBuyerSeat(String buyerSeat) {
        this.buyerSeat = buyerSeat;
    }
}