package main.controller;


import main.controller.utils.ControllerUtils;
import main.dto.RoleDTO;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.Role;
import main.service.IRolesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api")
public class RolesController {

    private static final Logger logger = LoggerFactory.getLogger(RolesController.class);
    private static final String MESSAGE = "message";


    IRolesService rolesService;

    @Autowired
    public RolesController(IRolesService rolesService) {
        this.rolesService = rolesService;
    }

    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/roles")
    public ResponseEntity<Object> getAllRoles() {
        try {
            List<Role> roles = rolesService.getRoles();

            if (roles == null || roles.isEmpty())
                return ResponseEntity.noContent().build();

            List<RoleDTO> roleDTOS = ControllerUtils.mapRoleListToRoleDtoList(roles);

            return ResponseEntity.ok().body(roleDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all roles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @GetMapping("/admin/rolespag")
    public ResponseEntity<Object> getAllRoles(@RequestHeader("Language") String language, @RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Role> rolesPage = rolesService.getRoles(pageable);

            Page<RoleDTO> roleDTOS = ControllerUtils.mapRoleListToRoleDtoList(rolesPage);

            return ResponseEntity.status(HttpStatus.OK).body(roleDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all paginated roles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/createrole")
    public ResponseEntity<Object> createRole(@RequestBody RoleDTO roleDTO) {
        try {
            Role role = new Role();
            role.setId(roleDTO.getId());
            role.setName(roleDTO.getName());

            rolesService.addRole(role);

            List<Role> roles = rolesService.getRoles();
            if (roles == null || roles.isEmpty())
                return ResponseEntity.noContent().build();

            roleDTO.setId(role.getId());

            return ResponseEntity.ok().body(roleDTO);
        }
        catch (FieldNotUniqueOrNullException a) {
            logger.error("Field unique or not null error: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error creating role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/searchroles")
    public ResponseEntity<Object> searchRoles(@RequestHeader("Language") String language, @RequestBody RoleDTO roleDTO) {
        try {
            logger.info("RoleDTO: {}", roleDTO);
            List<Role> roles = rolesService.searchRoles(roleDTO);
            if (roles == null || roles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            List<RoleDTO> roleDTOS = ControllerUtils.mapRoleListToRoleDtoList(roles);
            logger.info("RoleDTOS: {}", roleDTOS);
            return ResponseEntity.status(HttpStatus.OK).body(roleDTOS);
        }
        catch (Exception e) {
            logger.error("Error searching roles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/updaterole")
    public ResponseEntity<Object> updateRole(@RequestBody RoleDTO roleDTO) {
        try {

            Role role = new Role();
            role.setId(roleDTO.getId());
            role.setName(roleDTO.getName());

            rolesService.updateRole(role);
            return ResponseEntity.ok().body(roleDTO);
        }
        catch (FieldNotUniqueOrNullException a) {
            logger.error("Field not unqiue or null: {}", a.getMessage(), a);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error updating role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/admin/roles/id/{id}")
    public ResponseEntity<Object> getRoleById(@PathVariable("id") Long id) {
        try {
            Role role = rolesService.getRoleById(id);

            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());

            return ResponseEntity.ok().body(roleDTO);

        }
        catch (Exception e) {
            logger.error("Error fetching roles by id: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/admin/roles/name/{name}")
    public ResponseEntity<Object> getRoleByName(@PathVariable("name") String name) {
        try {
            Role role = rolesService.getRoleByName(name);

            if (role == null)
                return ResponseEntity.notFound().build();

            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());

            return ResponseEntity.ok().body(roleDTO);

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/admin/deleterole")
    public ResponseEntity<Object> deleteRole(@RequestBody RoleDTO roleDTO) {
        try {
            rolesService.deleteRoleById(roleDTO.getId());
            List<Role> roles = rolesService.getRoles();
            logger.info("Deleting roles..");
            if (roles == null || roles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<RoleDTO> rolesDTOS = ControllerUtils.mapRoleListToRoleDtoList(roles);
            return ResponseEntity.status(HttpStatus.OK).body(rolesDTOS);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

}
