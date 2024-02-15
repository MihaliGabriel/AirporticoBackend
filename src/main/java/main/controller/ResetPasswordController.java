package main.controller;

import lombok.AllArgsConstructor;
import main.dto.ResetPasswordDTO;
import main.dto.UserDTO;
import main.dto.UsernameDTO;
import main.model.User;
import main.repository.ResetPasswordRepository;
import main.service.EmailService;
import main.service.IResetPasswordService;
import main.service.IUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/api/resetpassword")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ResetPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);
    private static final String MESSAGE = "message";

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Autowired
    private IUsersService usersService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IResetPasswordService resetPasswordService;




    @GetMapping("/token")
    public RedirectView getUserByToken(@RequestParam("token") String token) {

        String username = resetPasswordRepository.findUserByToken(token);
        User user = usersService.getUserByUsername(username);
        String redirectUrl = "http://localhost:4200/resetpassword?username=" + user.getUsername();

        return new RedirectView(redirectUrl);
    }

    @PostMapping("/sendresetemail")
    public ResponseEntity<String> sendResetPasswordEmail(@RequestBody UsernameDTO usernameDTO) {
        try {

            logger.info("UsernameDTO: {}", usernameDTO.getUsername());
            User user = usersService.getUserByUsername(usernameDTO.getUsername());
            emailService.sendResetPasswordEmail(user, "mihaligabriel75@gmail.com");

            return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"Email sent succesfully\"}");
        }
        catch (Exception e) {
            logger.error("Error sending reset password email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/saveresetpassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            logger.info("Reseting password...");
            User user = usersService.getUserByUsername(resetPasswordDTO.getUsername());
            UserDTO userDTO = new UserDTO();

            userDTO.setUsername(resetPasswordDTO.getUsername());
            userDTO.setPassword(resetPasswordDTO.getPassword());

            user.setPassword(userDTO.getPassword());

            usersService.updateUser(user);
            resetPasswordService.deleteResetPasswordByUsername(user.getUsername());

            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");

        }
        catch (Exception e) {
            logger.error("Error updating password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }


}