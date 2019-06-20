package hska.iwi.eShopMaster.model.database.dataAccessObjects;

import hska.iwi.eShopMaster.model.domain.User;
import hska.iwi.eShopMaster.model.domain.UserDto;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import static hska.iwi.eShopMaster.model.ApiConfig.API_USERS;
import static java.util.Objects.requireNonNull;

public class UserDAO {

    private final RestTemplate restTemplate = new RestTemplate();

    @Nullable
    public User getUserByUsername(String name) {
        User[] users = requireNonNull(restTemplate.getForObject(API_USERS, User[].class));
        for (User user : users) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        return null;
    }

    public User createUser(UserDto userDto) {
        return restTemplate.postForObject(API_USERS, userDto, User.class);
    }

    public void deleteUserById(int id) {
        restTemplate.delete(API_USERS + "/" + id);
    }

}
