package de.hska.iwi.vslab.edgeserver;

import de.hska.iwi.vslab.edgeserver.model.NewUser;
import de.hska.iwi.vslab.edgeserver.model.UserCore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@RestController
@RequestMapping("user-api/users")
public class UserController {

    public static final String API_USERS = "http://user-service:8080/users";

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public UserCore createUser(@RequestBody @Valid NewUser newUser) {
        return restTemplate.postForObject(API_USERS, newUser, UserCore.class);
    }

    @GetMapping
    public String doesUserAlreadyExist(@RequestParam String username) {
        UserCore[] users = restTemplate.getForObject(API_USERS, UserCore[].class);
        for (UserCore user : users) {
            if (user.getUsername().equals(username)) {
                return "true";
            }
        }
        return "false";
    }

}
