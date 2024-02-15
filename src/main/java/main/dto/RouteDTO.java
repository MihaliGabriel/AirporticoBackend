package main.dto;

public class RouteDTO {


    private Long id;

    private String departureAirport;
    private String arrivalAirport;

    private String routeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAiport) {
        this.departureAirport = departureAiport;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String arrivalAirport, String departureAirport) {
        this.routeName = this.getId() + " - " + departureAirport + " - " + arrivalAirport;
    }
}
