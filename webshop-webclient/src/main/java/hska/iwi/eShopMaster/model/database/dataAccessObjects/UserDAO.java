package hska.iwi.eShopMaster.model.database.dataAccessObjects;

import hska.iwi.eShopMaster.model.domain.User;
import hska.iwi.eShopMaster.model.domain.UserDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static hska.iwi.eShopMaster.model.ApiConfig.API_USERS;
import static java.util.Objects.requireNonNull;

public class UserDAO {

    private final RestTemplate restTemplate = new RestTemplate();

    public User createUser(UserDto userDto) {
        HttpEntity<UserDto> userEntity = new HttpEntity<>(userDto);
        ResponseEntity<User> userResponseEntity = restTemplate.exchange(
                API_USERS, HttpMethod.POST, userEntity, User.class);
        return requireNonNull(userResponseEntity.getBody());
    }

}
