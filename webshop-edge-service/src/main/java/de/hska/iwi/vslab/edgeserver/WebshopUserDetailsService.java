package de.hska.iwi.vslab.edgeserver;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WebshopUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return mockUser(username);
    }

    private UserDetails mockUser(String username) {
        String userName = "test@test.com";
        String userPass = "tester";

        if (!userName.equals(username)) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return User.withUsername(username)
                .password("{noop}" + userPass)
                .roles("USER")
                .build();
    }

}
