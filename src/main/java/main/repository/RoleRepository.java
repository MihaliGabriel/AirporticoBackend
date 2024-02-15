package main.repository;

import main.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

    Long countByName(String name);

    @Query("SELECT r from Role r WHERE " +
            "(:roleName IS NULL OR LOWER(r.name) LIKE LOWER(concat('%', :roleName, '%')))")
    List<Role> search(@Param("roleName") String roleName);
}
