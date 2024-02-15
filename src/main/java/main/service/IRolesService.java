package main.service;

import main.dto.RoleDTO;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRolesService {
    List<Role> getRoles();

    Page<Role> getRoles(Pageable pageable);

    void addRole(Role role) throws FieldNotUniqueOrNullException;

    Role getRoleById(Long id);

    Role getRoleByName(String name);

    void updateRole(Role role) throws FieldNotUniqueOrNullException;

    void deleteRoleById(Long id);

    List<Role> searchRoles(RoleDTO roleDTO);

}
