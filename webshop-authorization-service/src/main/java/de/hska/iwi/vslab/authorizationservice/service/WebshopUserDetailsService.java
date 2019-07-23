package de.hska.iwi.vslab.authorizationservice.service;

import de.hska.iwi.vslab.authorizationservice.db.UserRepository;
import de.hska.iwi.vslab.authorizationservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service("userService")
public class WebshopUserDetailsService implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(WebshopUserDetailsService.class.getSimpleName());

    private final UserRepository userRepository;

    @Autowired
    public WebshopUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Logging in user with name '" + username + "'");
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.orElseThrow(() -> new UsernameNotFoundException("User with name '" + username + "' not found"));
    }

}
