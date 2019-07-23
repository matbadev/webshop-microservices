package de.hska.iwi.vslab.webshopcoreuserservice.api;

import de.hska.iwi.vslab.webshopcoreuserservice.db.RoleRepository;
import de.hska.iwi.vslab.webshopcoreuserservice.db.UserRepository;
import de.hska.iwi.vslab.webshopcoreuserservice.model.NewUser;
import de.hska.iwi.vslab.webshopcoreuserservice.model.Role;
import de.hska.iwi.vslab.webshopcoreuserservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(DebugUserController.class.getSimpleName());

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid NewUser newUser) {
        Optional<User> userOpt = userRepository.findByUsername(newUser.username);
        if (userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

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

}
