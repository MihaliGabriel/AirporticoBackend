package main.service;

import main.dto.UserDTO;
import main.model.*;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.repository.PersonRepository;
import main.repository.TicketRepository;
import main.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService implements IUsersService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public IRolesService rolesService;

    @Autowired
    public ITicketService ticketService;

    @Autowired
    public PersonRepository personRepository;

    @Autowired
    public IPersonService personService;

    @Autowired
    public TicketRepository ticketRepository;

    private static final String INSERT = "INSERT";

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    private boolean checkUniqueUsername(String username, String type) {
        Long count = userRepository.countByUsername(username);
        if (count != 0 && type.equals(INSERT))
            return false;
        if (count > 1 && type.equals("UPDATE")) {
            return false;
        }
        return true;
    }

    private boolean checkNullUsername(String username) {
        return username == null || username.isEmpty();
    }

    private boolean checkNullRole(Role role) {
        return role == null;
    }

    public void addUser(User user) throws FieldNotUniqueOrNullException {
        if (checkNullUsername(user.getUsername()))
            throw new FieldNotUniqueOrNullException("Please enter a username");
        if (!checkUniqueUsername(user.getUsername(), INSERT)) {
            throw new FieldNotUniqueOrNullException("Username not unique");
        }
        if (checkNullRole(user.getRole())) {
            throw new FieldNotUniqueOrNullException("Please enter a role");
        }
        userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserByTicket(Long ticketId) {
        return userRepository.findUserByTicket(ticketId);
    }

    public List<User> getUsersByFlight(Long flightId) {
        return userRepository.findUsersByFlight(flightId);
    }

    public User getUserById(Long id) {
        return userRepository.getById(id);
    }

    /**
     * @Author GXM
     * Stergere cascadata, se sterg mai intai biletele asociate user-ului si dupa user-ul.
     * @param id
     */
    public void deleteUserById(Long id) {
        List<Ticket> tickets = ticketRepository.findTicketsByUser(id);
        Person person = personRepository.findPersonByUser(id);
        if (person != null) {
            personService.deletePerson(person.getId());
        }
        if (tickets != null && !tickets.isEmpty()) {
            for (Ticket ticket : tickets) {
                logger.info("Ticket: {}", ticket);
                ticketService.deleteTicketById(ticket.getId());
            }
        }
        userRepository.deleteById(id);
    }

    public void updateUser(User user) throws FieldNotUniqueOrNullException {

        User userFromDb = userRepository.getById(user.getId());
        if (checkNullUsername(user.getUsername()))
            throw new FieldNotUniqueOrNullException("Username is null");
        if (!userFromDb.getUsername().equals(user.getUsername()) && !checkUniqueUsername(user.getUsername(), INSERT)) {
            throw new FieldNotUniqueOrNullException("Username not unique");
        }
        userRepository.save(user);
    }

    @Override
    public List<User> searchUser(UserDTO userDTO) {
        String username = userDTO.getUsername() != null ? userDTO.getUsername().toLowerCase() : null;
        String roleName = null;
        String password = null;

        if (userDTO.getRoleName() != null) {
            roleName = userDTO.getRoleName();
        }

        return userRepository.search(username, roleName, password);
    }
}
