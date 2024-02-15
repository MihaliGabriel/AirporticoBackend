package main.model;

import main.utils.Util;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="users",
        indexes = {@Index(name = "username_idx", columnList = "username", unique = true),
                    @Index(name = "password_idx", columnList = "password", unique = false)})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", length=100, nullable = false, unique = true)
    private String username;

    @Column(name="password", length=100, nullable = false, unique = false)
    private String password;

    @ManyToOne
    @JoinColumn(name="ref_role", nullable = false)
    private Role role;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
