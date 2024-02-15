package main.service;

import main.model.ResetPassword;
import main.repository.ResetPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ResetPasswordService implements IResetPasswordService {
    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    public String findByToken(String token) {
        return resetPasswordRepository.findUserByToken(token);
    }

    public String findBySmsToken(Integer smsToken) {
        return resetPasswordRepository.findUserBySmsToken(smsToken);
    }

    public void insertSmsToken(String username, Integer smsToken) {

        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setSmsToken(smsToken);
        resetPassword.setUsername(username);
        resetPasswordRepository.save(resetPassword);
    }

    @Override
    public Integer findSmsTokenByUsername(String username) {
        return resetPasswordRepository.findSmsTokenByUser(username);
    }

    @Override
    public void deleteResetPasswordByUsername(String username) {
        resetPasswordRepository.deleteByUsername(username);
    }
}
