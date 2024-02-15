package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.UserDTO;
import main.model.Ticket;
import main.model.User;
import main.service.IRolesService;
import main.service.ITicketService;
import main.service.IUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private static final String MESSAGE = "message";

    IUsersService usersService;
    IRolesService rolesService;
    ITicketService ticketService;

    @Autowired
    public UsersController(IUsersService usersService, IRolesService rolesService, ITicketService ticketService) {
        this.usersService = usersService;
        this.rolesService = rolesService;
        this.ticketService = ticketService;
    }

    /**
     * @Author GXM
     * @return Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     *      * care sunt prelucrate ca si erori de front-end.
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/users")
    public ResponseEntity<Object> getAllUsers(@RequestHeader("Language") String language) {
        try {
            List<User> users = usersService.getUsers();

            if (users == null || users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<UserDTO> userDTOS = ControllerUtils.mapUserListToUserDtoList(users);
            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/admin/users")
    public ResponseEntity<Object> getAllUsers(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> usersPage = usersService.getUsers(pageable);

            Page<UserDTO> userDTOS = ControllerUtils.mapUserListToUserDtoList(usersPage);
            return ResponseEntity.status(HttpStatus.OK).body(userDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching paginated users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/searchusers")
    public ResponseEntity<Object> searchUsers(@RequestHeader("Language") String language, @RequestBody UserDTO userDTO) {
        try {
            logger.info("UserDTO: {}", userDTO);
            List<User> users = usersService.searchUser(userDTO);

            if (users == null || users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<UserDTO> userDTOS = ControllerUtils.mapUserListToUserDtoList(users);

            logger.info("UserDTOS: {}", userDTOS);
            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("Error fetching users by search: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/admin/users/id/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") long id) {
        try {
            User user = usersService.getUserById(id);

            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                UserDTO userDTO = ControllerUtils.mapUserToUserDto(user);
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }
        }
        catch (Exception e) {
            logger.error("Error fetching user by id: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/admin/users/username/{username}")
    public ResponseEntity<Object> getUserByUsername(@PathVariable("username") String username) {
        try {
            User user = usersService.getUserByUsername(username);

            UserDTO userDTO = ControllerUtils.mapUserToUserDto(user);
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        }
        catch (Exception e) {
            logger.error("Error fetching users by username: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/createuser")
    public ResponseEntity<Object> createUser(@RequestBody UserDTO userDTO) {
        try {
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setRole(rolesService.getRoleByName(userDTO.getRoleName()));

            usersService.addUser(user);

            List<User> users = usersService.getUsers();


            if (users == null || users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            userDTO = ControllerUtils.mapUserToUserDto(user);
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/deleteuser")
    public ResponseEntity<Object> deleteUser(@RequestBody UserDTO userDTO) {
        try {
            usersService.deleteUserById(userDTO.getId());
            List<User> users = usersService.getUsers();
            logger.info("Deleting user..");
            if (users == null || users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<UserDTO> userDTOS = ControllerUtils.mapUserListToUserDtoList(users);
            return ResponseEntity.status(HttpStatus.OK).body(userDTOS);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("admin/updateuser")
    public ResponseEntity<Object> updateUser(@RequestBody UserDTO userDTO) {
        try {
            logger.info("updating user..");
            logger.info("UserDTO: {}", userDTO);
            User user = new User();
            user.setId(userDTO.getId());
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setRole(rolesService.getRoleByName(userDTO.getRoleName()));

            usersService.updateUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/fetchusers")
    public String showListUsersAdmin(Model model) {
        model.addAttribute("listUsers", new UserDTO());
        return "console";
    }

    @PostMapping(value = "/fetchusers")
    public String listUsersAdmin(Model model,
                                  HttpServletRequest request, HttpServletResponse response) {
        List<User> listUsers = usersService.getUsers();
        model.addAttribute("listUsers", listUsers);
        logger.info("User list was added");

        return "console";
    }

    @GetMapping("/admin/finduserticket/{id}")
    public ResponseEntity<Object> getUserByTicket(@PathVariable("id") Long id) {
        try {
            Ticket ticket = ticketService.getTicketById(id);
            User user = usersService.getUserByTicket(id);
            if (ticket == null || user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            UserDTO userDTO = ControllerUtils.mapUserToUserDto(user);

            return ResponseEntity.status(HttpStatus.OK).body(userDTO);

        }
        catch (Exception e) {
            logger.error("Error fetching users by ticket: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

}
