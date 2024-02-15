package main.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SmsService implements ISmsService {

    @Autowired
    private ResetPasswordService resetPasswordService;

    private Integer smsToken;

    Random rand = new Random();

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    /**
     * @Author GXM
     * Pentru a trimite mesaje, este folosit un API numit Twilio.
     * @param username
     * @return
     */
    public String send(String username) {
        final String ACCOUNT_SID = "ACfee360c6ce60939347cd01b6791d4960";
        final String AUTH_TOKEN = "b86eef3ae3ecd0e205c8d66a5a87a60e";
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        for (int i = 1; i <= 10; i++) {
            smsToken = rand.nextInt((9999 - 100) + 1) + 10;
            logger.info("Sms token : {}", smsToken);
        }
        String message = smsToken.toString();

        Message mess = Message.creator(
                new PhoneNumber("+40757715362"),
                new PhoneNumber("+18159120838"),
                message).create();

        resetPasswordService.insertSmsToken(username, smsToken);
        logger.info("{}", mess.getSid());
        return message;
    }
}