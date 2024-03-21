package main.dto;

public class PassengerDTO {
    private Long id;
    private Long ticketId;
    private String ticketName;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Integer nrBigLuggage = 0;
    private Integer nrMediumLuggage = 0;
    private Integer nrSmallLuggage = 0;
    private String seat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String flightName) {
        this.ticketName = this.getId() + "-" + flightName;
    }

    public Integer getNrBigLuggage() {
        return nrBigLuggage;
    }

    public void setNrBigLuggage(Integer nrBigLuggage) {
        this.nrBigLuggage = nrBigLuggage;
    }

    public Integer getNrMediumLuggage() {
        return nrMediumLuggage;
    }

    public void setNrMediumLuggage(Integer nrMediumLuggage) {
        this.nrMediumLuggage = nrMediumLuggage;
    }

    public Integer getNrSmallLuggage() {
        return nrSmallLuggage;
    }

    public void setNrSmallLuggage(Integer nrSmallLuggage) {
        this.nrSmallLuggage = nrSmallLuggage;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "PassengerDTO{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", ticketName='" + ticketName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", nrBigLuggage=" + nrBigLuggage +
                ", nrMediumLuggage=" + nrMediumLuggage +
                ", nrSmallLuggage=" + nrSmallLuggage +
                ", seat='" + seat + '\'' +
                '}';
    }
}
