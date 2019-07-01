package de.hska.iwi.vslab.edgeserver;

import de.hska.iwi.vslab.edgeserver.model.UserCore;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public class WebshopAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = Logger.getLogger("WebshopUserDetailsService");

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        Optional<UserCore> userOpt = getUserByUsername(username);
        UserCore user = userOpt.orElseThrow(() -> new UsernameNotFoundException("User with name '" + username + "' not found"));

        if (!user.getPassword().equals(password)) {
            throw new BadCredentialsException("Wrong password for user '" + username + '"');
        }

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                List.of(user)
        );
    }

    private Optional<UserCore> getUserByUsername(String username) {
        UserCore[] users = requireNonNull(restTemplate.getForObject(UserController.API_USERS, UserCore[].class));
        logger.info("Loaded " + users.length + " users: " + Arrays.deepToString(users));
        for (UserCore user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

}
