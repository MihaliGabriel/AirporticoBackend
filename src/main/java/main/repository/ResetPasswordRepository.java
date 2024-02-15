package main.repository;

import main.model.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    @Query("select rp.username from ResetPassword rp WHERE rp.token = :token")
    String findUserByToken(String token);

    @Query("select rp.username from ResetPassword rp WHERE rp.smsToken = :smsToken")
    String findUserBySmsToken(Integer smsToken);

    @Query("select rp.smsToken from ResetPassword rp WHERE rp.username = :username")
    Integer findSmsTokenByUser(String username);

    void deleteByUsername(String username);
}
