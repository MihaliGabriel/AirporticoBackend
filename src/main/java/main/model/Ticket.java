package main.model;

import main.utils.Util;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="ticket_price", nullable = false, unique = false, columnDefinition = "NUMERIC(10,2)")
    private Double price;

    @ManyToOne
    @JoinColumn(name="ref_user", nullable = false)
    User user;

    @Column(name="ticket_type", nullable = false, unique = false)
    private String ticketType;

    @Convert(converter = TicketStatusConverter.class)
    @Column(name="ticket_status", nullable = false, unique = false)
    private TicketStatus ticketStatus;

    @ManyToOne
    @JoinColumn(name="ref_flight", nullable = false)
    Flight flight;

    @CreationTimestamp
    @Column(name="created_at", nullable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = Util.getDouble(price);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", price=" + price +
                ", user=" + user +
                ", ticketType='" + ticketType + '\'' +
                ", ticketStatus=" + ticketStatus +
                ", flight=" + flight +
                '}';
    }
}