package main.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import main.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Entity
@Table(name="flights", indexes = {@Index(name="flight_name_idx", columnList = "flight_name", unique=true),
        @Index(name="arrival_date_idx", columnList = "arrival_date", unique=false),
        @Index(name="departure_date_idx", columnList = "departure_date", unique=false)})
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="flight_name", length=100, unique = true, nullable = false)
    private String name;

    @Column(name="flight_first_class_seats", unique = false, nullable = false)
    private Integer firstClassSeats;

    @Column(name="flight_business_seats", unique = false, nullable = false)
    private Integer businessSeats;

    @Column(name="flight_economy_seats", unique = false, nullable = false)
    private Integer economySeats;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="arrival_date", unique=false, nullable=false)
    private LocalDateTime arrivalDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="departure_date", unique=false, nullable=false)
    private LocalDateTime departureDate;

    @Column(name="flight_first_class_price", nullable = false, unique = false, columnDefinition = "NUMERIC(10,2)")
    private Double firstClassPrice;

    @Column(name="flight_business_price", nullable = false, unique = false, columnDefinition = "NUMERIC(10,2)")
    private Double businessPrice;

    @Column(name="flight_economy_price", nullable = false, unique = false, columnDefinition = "NUMERIC(10,2)")
    private Double economyPrice;

    @ManyToOne
    @JoinColumn(name="ref_company", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name="ref_airplane", nullable = false)
    private Airplane airplane;

    @ManyToOne
    @JoinColumn(name="ref_route", nullable = false)
    private Route route;

    @Column(name="flight_remaining_business_seats", unique = false, nullable = true)
    private Integer remainingBusinessSeats;

    @Column(name="flight_remaining_economy_seats", unique = false, nullable = true)
    private Integer remainingEconomySeats;

    @Column(name="flight_remaining_first_class_seats", unique = false, nullable = true)
    private Integer remainingFirstClassSeats;


    @Column(name = "duration_in_seconds")
    private long durationInSeconds;

    @Transient
    private double discountPercentage;
    @Transient
    private AirplaneSeatType[][] occupiedSeats;

    private static final Logger logger = LoggerFactory.getLogger(Flight.class);

    @PostLoad
    public void computeTransientValues() {
        computeDiscount();
    }

    public void computeDiscount() {
        LocalDate now = LocalDate.now();
        long daysDifference = ChronoUnit.DAYS.between(now, this.departureDate.toLocalDate());
        logger.info("Days difference: {}", daysDifference);
        if (daysDifference < 7) {
            this.discountPercentage = 0;
        } else if (daysDifference < 30) {
            this.discountPercentage = 10;
        } else if (daysDifference < 90) {
            this.discountPercentage = 20;
        } else {
            this.discountPercentage = 30;
        }
    }

    public Duration getDuration() {
        return Duration.ofSeconds(durationInSeconds);
    }

    public void setDuration(Duration duration) {
        this.durationInSeconds = duration.getSeconds();
    }

    public void computeDuration() {
        if (departureDate != null && arrivalDate != null) {
            Duration computedDuration = Duration.between(departureDate, arrivalDate);
            setDuration(computedDuration);
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Integer getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(Number businessSeats) {
        this.businessSeats = Util.getInteger(businessSeats);
    }

    public Integer getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(Number economySeats) {
        this.economySeats = Util.getInteger(economySeats);
    }

    public Double getBusinessPrice() {
        return businessPrice;
    }

    public void setBusinessPrice(Number businessPrice) {
        this.businessPrice = Util.getDouble(businessPrice);
    }

    public Double getEconomyPrice() {
        return economyPrice;
    }

    public void setEconomyPrice(Number economyPrice) {
        this.economyPrice = Util.getDouble(economyPrice);
    }

    public double getDiscountedBusinessPrice() {
        return businessPrice - (businessPrice * discountPercentage / 100);
    }

    public double getDiscountedEconomyPrice() {
        return economyPrice - (economyPrice * discountPercentage / 100);
    }

    public double getDiscountedFirstClassPrice() {
        return firstClassPrice - (firstClassPrice * discountPercentage / 100);
    }

    public Integer getRemainingBusinessSeats() {
        return remainingBusinessSeats;
    }

    public Integer getFirstClassSeats() {
        return firstClassSeats;
    }

    public void setFirstClassSeats(Number firstClassSeats) {
        this.firstClassSeats = Util.getInteger(firstClassSeats);
    }

    public Integer getRemainingFirstClassSeats() {
        return remainingFirstClassSeats;
    }

    public void setRemainingFirstClassSeats(Integer remainingFirstClassSeats) {
        this.remainingFirstClassSeats = remainingFirstClassSeats;
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

    public Double getFirstClassPrice() {
        return firstClassPrice;
    }

    public void setFirstClassPrice(Number firstClassPrice) {
        this.firstClassPrice = Util.getDouble(firstClassPrice);
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public AirplaneSeatType[][] getOccupiedSeats() {
        if (occupiedSeats.length == 0)
            return new AirplaneSeatType[airplane.getColumns()][airplane.getRows()];
        return occupiedSeats;
    }

    public void setOccupiedSeats(AirplaneSeatType[][] occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstClassSeats=" + firstClassSeats +
                ", businessSeats=" + businessSeats +
                ", economySeats=" + economySeats +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                ", firstClassPrice=" + firstClassPrice +
                ", businessPrice=" + businessPrice +
                ", economyPrice=" + economyPrice +
                ", company=" + company +
                ", airplane=" + airplane +
                ", route=" + route +
                ", remainingBusinessSeats=" + remainingBusinessSeats +
                ", remainingEconomySeats=" + remainingEconomySeats +
                ", remainingFirstClassSeats=" + remainingFirstClassSeats +
                ", durationInSeconds=" + durationInSeconds +
                ", discountPercentage=" + discountPercentage +
                ", occupiedSeats=" + Arrays.toString(occupiedSeats) +
                '}';
    }
}

