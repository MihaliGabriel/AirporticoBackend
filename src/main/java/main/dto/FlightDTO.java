package main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import main.utils.LocalDateTimeDeserializer;
import main.utils.LocalDateTimeSerializer;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class FlightDTO {
    private Long id;
    private String name;

    @NotBlank(message = "Departure date is required")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime departureDate;


    @NotBlank(message = "Arrival date is required")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime arrivalDate;

    @NotBlank(message = "Route id is required")
    private Long routeId;
    @NotBlank(message = "Company id is required")
    private Long companyId;

    private String routeName;

    private String arrivalCity;

    private String departureCity;

    private Integer remainingBusinessSeats;

    private Integer remainingEconomySeats;

    private Integer remainingFirstClassSeats;

    @NotBlank(message = "First class price is required")
    private Double firstClassPrice;
    @NotBlank(message = "Business class price is required")
    private Double businessPrice;
    @NotBlank(message = "Economy class price is required")
    private Double economyPrice;

    private Double discountedBusinessPrice;

    private Double discountedEconomyPrice;

    private Double discountedFirstClassPrice;

    private Double discountPercentage;

    private String companyName;

    private String duration;

    private Integer nrOfPassengers;

    private Double businessPriceMin;

    private Double businessPriceMax;

    private Double economyPriceMin;

    private Double economyPriceMax;

    private Double firstClassPriceMin;

    private Double firstClassPriceMax;

    private String sortByDuration;

    private boolean[][] occupiedSeats;
    @NotBlank(message = "Airplane name is required")
    private String airplaneName;
    @NotBlank(message = "Economy seats is required")
    private Integer economySeats;
    @NotBlank(message = "First class seats is required")
    private Integer firstClassSeats;
    @NotBlank(message = "Business class seats is required")
    private Integer businessSeats;

    public boolean[][] getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(boolean[][] occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String arrivalAirport, String departureAirport) {
        this.routeName = getRouteId() + " - " + arrivalAirport + " - " + departureAirport;
    }


    public String getAirplaneName() {
        return airplaneName;
    }

    public void setAirplaneName(String airplaneName) {
        this.airplaneName = airplaneName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Integer getNrOfPassengers() {
        return nrOfPassengers;
    }

    public void setNrOfPassengers(Integer nrOfPassengers) {
        this.nrOfPassengers = nrOfPassengers;
    }

    public Integer getRemainingBusinessSeats() {
        return remainingBusinessSeats;
    }

    public void setRemainingBusinessSeats(Integer remainingBusinessSeats) {
        this.remainingBusinessSeats = remainingBusinessSeats;
    }

    public Integer getRemainingEconomySeats() {
        return remainingEconomySeats;
    }

    public void setRemainingEconomySeats(Integer remainingEconomySeats) {
        this.remainingEconomySeats = remainingEconomySeats;
    }

    public Double getBusinessPrice() {
        return businessPrice;
    }

    public void setBusinessPrice(Double businessPrice) {
        this.businessPrice = businessPrice;
    }

    public Double getEconomyPrice() {
        return economyPrice;
    }

    public void setEconomyPrice(Double economyPrice) {
        this.economyPrice = economyPrice;
    }


    public Integer getRemainingFirstClassSeats() {
        return remainingFirstClassSeats;
    }

    public void setRemainingFirstClassSeats(Integer remainingFirstClassSeats) {
        this.remainingFirstClassSeats = remainingFirstClassSeats;
    }

    public Double getFirstClassPrice() {
        return firstClassPrice;
    }

    public void setFirstClassPrice(Double firstClassPrice) {
        this.firstClassPrice = firstClassPrice;
    }

    public double getDiscountedBusinessPrice() {
        return discountedBusinessPrice;
    }

    public void setDiscountedBusinessPrice(double discountedBusinessPrice) {
        this.discountedBusinessPrice = discountedBusinessPrice;
    }

    public double getDiscountedEconomyPrice() {
        return discountedEconomyPrice;
    }

    public void setDiscountedEconomyPrice(double discountedEconomyPrice) {
        this.discountedEconomyPrice = discountedEconomyPrice;
    }

    public double getDiscountedFirstClassPrice() {
        return discountedFirstClassPrice;
    }

    public void setDiscountedFirstClassPrice(double discountedFirstClassPrice) {
        this.discountedFirstClassPrice = discountedFirstClassPrice;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDiscountedBusinessPrice(Double discountedBusinessPrice) {
        this.discountedBusinessPrice = discountedBusinessPrice;
    }

    public void setDiscountedEconomyPrice(Double discountedEconomyPrice) {
        this.discountedEconomyPrice = discountedEconomyPrice;
    }

    public void setDiscountedFirstClassPrice(Double discountedFirstClassPrice) {
        this.discountedFirstClassPrice = discountedFirstClassPrice;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Double getBusinessPriceMin() {
        return businessPriceMin;
    }

    public void setBusinessPriceMin(Double businessPriceMin) {
        this.businessPriceMin = businessPriceMin;
    }

    public Double getBusinessPriceMax() {
        return businessPriceMax;
    }

    public void setBusinessPriceMax(Double businessPriceMax) {
        this.businessPriceMax = businessPriceMax;
    }

    public Double getEconomyPriceMin() {
        return economyPriceMin;
    }

    public void setEconomyPriceMin(Double economyPriceMin) {
        this.economyPriceMin = economyPriceMin;
    }

    public Double getEconomyPriceMax() {
        return economyPriceMax;
    }

    public void setEconomyPriceMax(Double economyPriceMax) {
        this.economyPriceMax = economyPriceMax;
    }

    public Double getFirstClassPriceMin() {
        return firstClassPriceMin;
    }

    public void setFirstClassPriceMin(Double firstClassPriceMin) {
        this.firstClassPriceMin = firstClassPriceMin;
    }

    public Double getFirstClassPriceMax() {
        return firstClassPriceMax;
    }

    public void setFirstClassPriceMax(Double firstClassPriceMax) {
        this.firstClassPriceMax = firstClassPriceMax;
    }

    public String getSortByDuration() {
        return sortByDuration;
    }

    public void setSortByDuration(String sortByDuration) {
        this.sortByDuration = sortByDuration;
    }

    public Integer getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(Integer economySeats) {
        this.economySeats = economySeats;
    }

    public Integer getFirstClassSeats() {
        return firstClassSeats;
    }

    public void setFirstClassSeats(Integer firstClassSeats) {
        this.firstClassSeats = firstClassSeats;
    }

    public Integer getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(Integer businessSeats) {
        this.businessSeats = businessSeats;
    }

    @Override
    public String toString() {
        return "FlightDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                ", routeId=" + routeId +
                ", companyId=" + companyId +
                ", routeName='" + routeName + '\'' +
                ", arrivalCity='" + arrivalCity + '\'' +
                ", departureCity='" + departureCity + '\'' +
                ", remainingBusinessSeats=" + remainingBusinessSeats +
                ", remainingEconomySeats=" + remainingEconomySeats +
                ", remainingFirstClassSeats=" + remainingFirstClassSeats +
                ", firstClassPrice=" + firstClassPrice +
                ", businessPrice=" + businessPrice +
                ", economyPrice=" + economyPrice +
                ", discountedBusinessPrice=" + discountedBusinessPrice +
                ", discountedEconomyPrice=" + discountedEconomyPrice +
                ", discountedFirstClassPrice=" + discountedFirstClassPrice +
                ", discountPercentage=" + discountPercentage +
                ", companyName='" + companyName + '\'' +
                ", duration='" + duration + '\'' +
                ", nrOfPassengers=" + nrOfPassengers +
                ", businessPriceMin=" + businessPriceMin +
                ", businessPriceMax=" + businessPriceMax +
                ", economyPriceMin=" + economyPriceMin +
                ", economyPriceMax=" + economyPriceMax +
                ", firstClassPriceMin=" + firstClassPriceMin +
                ", firstClassPriceMax=" + firstClassPriceMax +
                ", sortByDuration='" + sortByDuration + '\'' +
                '}';
    }
}

