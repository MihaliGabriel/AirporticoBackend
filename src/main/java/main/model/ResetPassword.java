package main.model;

import main.utils.Util;

import javax.persistence.*;

@Entity
@Table(name="reset_password")
public class ResetPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="username", unique = true, nullable = false)
    String username;

    @Column(name="sms_token", unique = true, nullable = true)
    Integer smsToken;

    @Column(name="token", unique = true, nullable = true)
    String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getSmsToken() {
        return smsToken;
    }

    public void setSmsToken(Number smsToken) {
        this.smsToken = Util.getInteger(smsToken);
    }

    @Override
    public String toString() {
        return "ResetPassword{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", smsToken='" + smsToken + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
