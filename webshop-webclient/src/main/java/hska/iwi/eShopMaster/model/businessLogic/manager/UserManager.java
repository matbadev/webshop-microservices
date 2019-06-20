package hska.iwi.eShopMaster.model.businessLogic.manager;

import hska.iwi.eShopMaster.model.domain.Role;
import hska.iwi.eShopMaster.model.domain.User;

public interface UserManager {

    User registerUser(String username, String firstname, String lastname, String password, String roletype);

    User getUserByUsername(String username);

    boolean deleteUserById(int id);

    Role getRoleByLevel(int level);

    boolean doesUserAlreadyExist(String username);

}
