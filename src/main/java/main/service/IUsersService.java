package main.service;

import main.dto.UserDTO;
import main.model.AuthenticationMethod;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

 public interface IUsersService {
     List<User> getUsers();

     Page<User> getUsers(Pageable pageable);
     User getUserByProviderIdAndProvider(String providerId, AuthenticationMethod authenticationMethod);
     void addUser(User user) throws FieldNotUniqueOrNullException;

     User getUserById(Long id);

     User getUserByUsername(String username);

     User getUserByTicket(Long ticketId);

     List<User> getUsersByFlight(Long flightId);

     void deleteUserById(Long id);

     void updateUser(User user) throws FieldNotUniqueOrNullException;

     List<User> searchUser(UserDTO userDTO);
}
