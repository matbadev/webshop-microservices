package hska.iwi.eShopMaster.model.database.dataAccessObjects;

import hska.iwi.eShopMaster.model.domain.User;
import hska.iwi.eShopMaster.model.domain.UserDto;
import org.springframework.web.client.RestTemplate;

import static hska.iwi.eShopMaster.model.ApiConfig.API_USERS;
import static java.util.Objects.requireNonNull;

public class UserDAO {

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean doesUserAlreadyExist(String username) {
        String existsString = requireNonNull(restTemplate.getForObject(API_USERS + "?username=" + username, String.class));
        return existsString.equals("true");
    }

    public User createUser(UserDto userDto) {
        return restTemplate.postForObject(API_USERS, userDto, User.class);
    }

}
