package main.model;

import main.utils.Util;

import javax.persistence.*;

@Entity
@Table(name="routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="ref_airport_arrival", nullable = false)
    private Airport arrivalAirport;

    @ManyToOne
    @JoinColumn(name="ref_airport_departure", nullable = false)
    private Airport departureAirport;

    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
    }
    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", arrivalAirport=" + arrivalAirport +
                ", departureAirport=" + departureAirport +
                '}';
    }
}

