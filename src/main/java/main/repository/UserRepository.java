package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Long countByUsername(String username);

    @Query("SELECT " +
            "u " +
            "from User u " +
            "inner join Ticket t " +
            "on u.id = t.user.id " +
            "inner join Flight f " +
            "on t.flight.id = f.id " +
            "where f.id = :flightId")
    List<User> findUsersByFlight(Long flightId);

    @Query("SELECT t.user from Ticket t where t.id = :ticketId")
    User findUserByTicket(Long ticketId);

    @Query("SELECT u from User u WHERE " +
            "(:username IS NULL OR LOWER(u.username) LIKE LOWER(concat('%', :username, '%'))) AND " +
            "(:password IS NULL OR LOWER(u.password) LIKE LOWER(concat('%', :password, '%'))) AND " +
            "(:roleName IS NULL OR LOWER(u.role.name) LIKE LOWER(concat('%', :roleName, '%')))")
    List<User> search(@Param("username") String username, @Param("roleName") String roleName, @Param("password") String password);

    @Query("SELECT u from User u inner join Role r on u.role.id = r.id where u.role.id = :roleId")
    List<User> findUserByRoleId(Long roleId);
}