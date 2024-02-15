package main.service;

public interface IResetPasswordService {
    String findByToken(String token);

    void deleteResetPasswordByUsername(String username);

    String findBySmsToken(Integer smsToken);

    void insertSmsToken(String username, Integer smsToken);

    Integer findSmsTokenByUsername(String username);
}
