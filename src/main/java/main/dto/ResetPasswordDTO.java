package main.dto;

public class ResetPasswordDTO {
    private String username;
    private String password;
    private Integer smsToken;

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

    public Integer getSmsToken() {
        return smsToken;
    }

    public void setSmsToken(Integer smsToken) {
        this.smsToken = smsToken;
    }
}
