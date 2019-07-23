package de.hska.iwi.vslab.webshopcoreuserservice.api;

import de.hska.iwi.vslab.webshopcoreuserservice.db.RoleRepository;
import de.hska.iwi.vslab.webshopcoreuserservice.db.UserRepository;
import de.hska.iwi.vslab.webshopcoreuserservice.model.Role;
import de.hska.iwi.vslab.webshopcoreuserservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
//@EnableHystrix
@RequestMapping("/debug")
public class DebugUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final Map<Integer, User> userCache = new HashMap<>();
    private final Map<Integer, Role> roleCache = new HashMap<>();

    @Autowired
    public DebugUserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    //    @HystrixCommand(fallbackMethod = "getAllUsersCache", commandProperties = {
//            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
//    })
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

    //    @HystrixCommand(fallbackMethod = "getUserByIdCache", commandProperties = {
//            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
//    })
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

    //    @HystrixCommand(fallbackMethod = "deleteUserByIdCache", commandProperties = {
//            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
//    })
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

    //    @HystrixCommand(fallbackMethod = "getAllRolesCache", commandProperties = {
//            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
//    })
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
