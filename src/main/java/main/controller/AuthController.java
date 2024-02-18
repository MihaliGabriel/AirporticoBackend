package main.controller;

import main.config.security.JWTGenerator;
import main.dto.*;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.User;
import main.service.IResetPasswordService;
import main.service.IRolesService;
import main.service.ISmsService;
import main.service.IUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private AuthenticationManager authenticationManager;
    private IUsersService usersService;
    private PasswordEncoder passwordEncoder;
    private IResetPasswordService resetPasswordService;
    private JWTGenerator jwtGenerator;
    private IRolesService rolesService;
    private ISmsService smsService;

    private static final String MESSAGE = "message";

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, IUsersService usersService, PasswordEncoder passwordEncoder, IResetPasswordService resetPasswordService, JWTGenerator jwtGenerator, IRolesService rolesService, ISmsService smsService) {
        this.authenticationManager = authenticationManager;
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
        this.resetPasswordService = resetPasswordService;
        this.jwtGenerator = jwtGenerator;
        this.rolesService = rolesService;
        this.smsService = smsService;
    }

    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterDTO registerDTO, Model model) {
        model.addAttribute("registerDTO", registerDTO);

        User user = new User();
        UserDTO userDTO = new UserDTO();
        logger.info("RegisterDTO username and password: {} {}", registerDTO.getUsername(), registerDTO.getPassword());
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(rolesService.getRoleByName("user"));

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRoleName(user.getRole().getName());
        try {
            usersService.addUser(user);
        }
        catch (FieldNotUniqueOrNullException a) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, "User cannot be registered"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PostMapping(value = "/changepassword")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        String username = changePasswordDTO.getUsername();
        String oldPassword = changePasswordDTO.getOldPassword();
        String newPassword = changePasswordDTO.getNewPassword();

        try {
            User user = usersService.getUserByUsername(username);

            if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
                logger.info("Change password user pass: {}", user.getPassword());
                user.setPassword(passwordEncoder.encode(newPassword));
                usersService.updateUser(user);
                return ResponseEntity.ok().body(Collections.singletonMap(MESSAGE, "Password changed successfully"));
            } else {
                return ResponseEntity.badRequest().body(Collections.singletonMap(MESSAGE, "Password change failed: Old password is incorrect"));
            }
        }
        catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO,
                                    HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("loginDTO", loginDTO);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                            loginDTO.getPassword()));

            logger.info("UserDTO login information: {} {} {} ", loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getTwoFactorAuth());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);

            logger.info("Login success! Token: {}", token);

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Map<String, Object> responseMap = new HashMap<>();

            responseMap.put("username", loginDTO.getUsername());
            responseMap.put("token", token);
            responseMap.put("roles", authorities);
            responseMap.put("id", usersService.getUserByUsername(loginDTO.getUsername()).getId());

            if (Boolean.TRUE.equals(loginDTO.getTwoFactorAuth())) {
                smsService.send(loginDTO.getUsername());
            }

            responseMap.put("smsToken", resetPasswordService.findSmsTokenByUsername(loginDTO.getUsername()));
            logger.info("Response map for login {}", responseMap);

            return ResponseEntity.ok().body(responseMap);

        }
        catch (AuthenticationException e) {
            logger.error("Error login: {}", e.getMessage(), e);
            e.printStackTrace();
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Login failed");
            errorMap.put(MESSAGE, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorMap);
        }
    }

    @PostMapping("/smsToken")
    public ResponseEntity<Object> loginWithSmsToken(@RequestBody ResetPasswordDTO resetPassword) {
        String associatedUsername = resetPasswordService.findBySmsToken(resetPassword.getSmsToken());
        User user = usersService.getUserByUsername(resetPasswordService.findBySmsToken(resetPassword.getSmsToken()));

        Map<String, Object> responseMap = new HashMap<>();

        if (associatedUsername != null && !associatedUsername.isEmpty()) {
            responseMap.put("verified", true);
            resetPasswordService.deleteResetPasswordByUsername(user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } else {
            responseMap.put("verified", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        }

    }

}
