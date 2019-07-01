package hska.iwi.eShopMaster.model.database.dataAccessObjects;

import hska.iwi.eShopMaster.model.domain.User;
import hska.iwi.eShopMaster.model.domain.UserDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static hska.iwi.eShopMaster.model.ApiConfig.API_USERS;

public class UserDAO {

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean doesUserAlreadyExist(String username) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_USERS)
                .queryParam("username", username)
                .build()
                .toUri();
        try {
            ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException ex) {
            return false;
        }
    }

    public User createUser(UserDto userDto) {
        return restTemplate.postForObject(API_USERS, userDto, User.class);
    }

}
