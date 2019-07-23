package de.hska.iwi.vslab.webshopcoreuserservice.api;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import de.hska.iwi.vslab.webshopcoreuserservice.db.RoleRepository;
import de.hska.iwi.vslab.webshopcoreuserservice.db.UserRepository;
import de.hska.iwi.vslab.webshopcoreuserservice.model.NewUser;
import de.hska.iwi.vslab.webshopcoreuserservice.model.Role;
import de.hska.iwi.vslab.webshopcoreuserservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@EnableHystrix
@RequestMapping("/")
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getSimpleName());

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final Map<Integer, User> userCache = new HashMap<>();
    private final Map<Integer, Role> roleCache = new HashMap<>();

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @HystrixCommand(fallbackMethod = "getAllUsersCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping("users")
    @RolesAllowed({"ROLE_DEBUG"})
    public Iterable<User> getAllUsers() {
        Iterable<User> users = userRepository.findAll();
        userCache.clear();
        users.forEach(user -> userCache.put(user.getId(), user));
        return users;
    }

    @SuppressWarnings("unused")
    public ResponseEntity<Iterable<User>> getAllUsersCache() {
        return ResponseEntity.ok(userCache.values());
    }

    @HystrixCommand(fallbackMethod = "createUserCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @PostMapping("users")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<User> createUser(@RequestBody @Valid NewUser newUser) {
        Role role = roleRepository.findByTypeEquals(newUser.roletype)
                .orElseGet(() -> createRole(newUser.roletype));
        User fullUser = new User(newUser.username, newUser.firstname, newUser.lastname, newUser.password, role);
        User createdUser = userRepository.save(fullUser);
        return ResponseEntity.ok(createdUser);
    }

    @SuppressWarnings("unused")
    public ResponseEntity<User> createUserCache(@RequestBody @Valid NewUser newUser) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    private Role createRole(String type) {
        LOGGER.info("Creating new role with type '" + type + "'");
        Role role = new Role(type, 5);
        return roleRepository.save(role);
    }

    @HystrixCommand(fallbackMethod = "getUserByIdCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping("users/{userId}")
    @RolesAllowed({"ROLE_DEBUG"})
    public ResponseEntity<User> getUserById(@PathVariable("userId") Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            userCache.putIfAbsent(userId, userOpt.get());
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @SuppressWarnings("unused")
    public ResponseEntity<User> getUserByIdCache(Integer userId) {
        try {
            User user = userCache.get(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @HystrixCommand(fallbackMethod = "deleteUserByIdCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @DeleteMapping("users/{userId}")
    @RolesAllowed({"ROLE_DEBUG"})
    public ResponseEntity deleteUserById(@PathVariable("userId") Integer userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @SuppressWarnings("unused")
    public ResponseEntity deleteUserByIdCache(Integer userId) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @HystrixCommand(fallbackMethod = "getAllRolesCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping("roles")
    @RolesAllowed({"ROLE_DEBUG"})
    public ResponseEntity<Iterable<Role>> getAllRoles() {

        roleCache.clear();
        roleRepository.findAll().forEach(role -> roleCache.put(role.getId(), role));

        return ResponseEntity.ok(roleRepository.findAll());
    }

    @SuppressWarnings("unused")
    public ResponseEntity<Iterable<Role>> getAllRolesCache() {
        return ResponseEntity.ok(roleCache.values());
    }

}
