package main.service;

import main.dto.RoleDTO;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.Role;
import main.model.User;
import main.repository.RoleRepository;
import main.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RolesService implements IRolesService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUsersService usersService;

    private static final Logger logger = LoggerFactory.getLogger(RolesService.class);

    private boolean checkUniqueName(String name, String type) {
        Long count = roleRepository.countByName(name);
        if (count != 0 && type.equals("INSERT"))
            return false;
        if (count > 1 && type.equals("UPDATE")) {
            return false;
        }
        return true;
    }

    private boolean checkNullName(String name) {
        return name == null || name.isEmpty();
    }

    public List<Role> getRoles() {
        List<Role> roles = roleRepository.findAll();
        logger.info("Roles: {}", roles);
        return roles;
    }

    @Override
    public Page<Role> getRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public void addRole(Role role) throws FieldNotUniqueOrNullException {
        if (checkNullName(role.getName())) {
            throw new FieldNotUniqueOrNullException("Name is null");
        }
        if (!checkUniqueName(role.getName(), "INSERT")) {
            throw new FieldNotUniqueOrNullException("Name is not unique");
        }
        roleRepository.save(role);
    }

    public Role getRoleById(Long id) {
        return roleRepository.getById(id);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public void updateRole(Role role) throws FieldNotUniqueOrNullException {
        if (checkNullName(role.getName())) {
            throw new FieldNotUniqueOrNullException("Name is null");
        }
        if (!checkUniqueName(role.getName(), "UPDATE")) {
            throw new FieldNotUniqueOrNullException("Name is not unique");
        }
        roleRepository.save(role);
    }

    /**
     * @Author GXM
     * Se sterg mai intai user-ii asociati rolului si dupa rolul.
     * @param id
     */
    public void deleteRoleById(Long id) {
        List<User> users = userRepository.findUserByRoleId(id);
        for (User u : users) {
            logger.info("{}", u.getUsername());
            usersService.deleteUserById(u.getId());
        }
        roleRepository.deleteById(id);
    }

    @Override
    public List<Role> searchRoles(RoleDTO roleDTO) {
        String roleName = roleDTO.getName() != null ? roleDTO.getName().toLowerCase() : null;
        return roleRepository.search(roleName);
    }

}
