package de.hska.iwi.vslab.webshopcoreuserservice.api;

import de.hska.iwi.vslab.webshopcoreuserservice.db.RoleRepository;
import de.hska.iwi.vslab.webshopcoreuserservice.db.UserRepository;
import de.hska.iwi.vslab.webshopcoreuserservice.model.NewUser;
import de.hska.iwi.vslab.webshopcoreuserservice.model.Role;
import de.hska.iwi.vslab.webshopcoreuserservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.logging.Logger;

@Controller
@RequestMapping("/")
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getSimpleName());

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("users")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("users")
    public ResponseEntity<User> createUser(@RequestBody @Valid NewUser newUser) {
        Role role = roleRepository.findByTypeEquals(newUser.roletype)
                .orElseGet(() -> createRole(newUser.roletype));
        User fullUser = new User(newUser.username, newUser.firstname, newUser.lastname, newUser.password, role);
        User createdUser = userRepository.save(fullUser);
        return ResponseEntity.ok(createdUser);
    }

    private Role createRole(String type) {
        LOGGER.info("Creating new role with type '" + type + "'");
        Role role = new Role(type, 5);
        return roleRepository.save(role);
    }

    @GetMapping("users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") int userId) {
        return ResponseEntity.of(userRepository.findById(userId));
    }

    @DeleteMapping("users/{userId}")
    public ResponseEntity deleteUserById(@PathVariable("userId") int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("roles")
    public ResponseEntity<Iterable<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

}
