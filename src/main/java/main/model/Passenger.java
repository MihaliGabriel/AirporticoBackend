package main.model;

import main.utils.Util;

import javax.persistence.*;

@Entity
@Table(name="passengers",
        indexes = {@Index(name="passenger_first_name_idx", columnList = "passenger_first_name", unique = false),
                    @Index(name="passenger_last_name_idx", columnList = "passenger_last_name", unique = false),
                    @Index(name="passenger_email_idx", columnList = "passenger_email", unique = false),
                    @Index(name="passenger_phone_number_idx", columnList = "passenger_phone_number", unique = false)})
public class Passenger {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="passenger_first_name", length=100, nullable = false, unique = false)
    private String firstName;

    @Column(name="passenger_last_name", length=100, nullable = false, unique = false)
    private String lastName;

    @Column(name="passenger_email", length=100, nullable = false, unique = false)
    private String email;

    @Column(name="passenger_phone_number", length=100, nullable = false, unique = false)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name="ref_ticket", nullable = false)
    private Ticket ticket;

    @Column(name="nr_small_luggage", nullable = true, unique = false)
    private Integer nrSmallLuggage = 0;

    @Column(name="nr_medium_luggage", nullable = true, unique = false)
    private Integer nrMediumLuggage = 0;

    @Column(name="nr_big_luggage", nullable = true, unique = false)
    private Integer nrBigLuggage = 0;

    @Column(name="passenger_seat", nullable = true, unique = false)
    private String seat;


    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
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

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Integer getNrSmallLuggage() {
        return nrSmallLuggage;
    }

    public void setNrSmallLuggage(Number nrSmallLuggage) {
        this.nrSmallLuggage = Util.getInteger(nrSmallLuggage);
    }

    public Integer getNrMediumLuggage() {
        return nrMediumLuggage;
    }

    public void setNrMediumLuggage(Number nrMediumLuggage) {
        this.nrMediumLuggage = Util.getInteger(nrMediumLuggage);
    }

    public Integer getNrBigLuggage() {
        return nrBigLuggage;
    }

    public void setNrBigLuggage(Number nrBigLuggage) {
        this.nrBigLuggage = Util.getInteger(nrBigLuggage);
    }

    public String getSeat() {

        if(seat == null)
            return "";
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", ticket=" + ticket +
                ", nrSmallLuggage=" + nrSmallLuggage +
                ", nrMediumLuggage=" + nrMediumLuggage +
                ", nrBigLuggage=" + nrBigLuggage +
                ", seat='" + seat + '\'' +
                '}';
    }
}
