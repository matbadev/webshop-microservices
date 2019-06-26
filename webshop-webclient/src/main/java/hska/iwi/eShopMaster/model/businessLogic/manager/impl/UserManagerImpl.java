package hska.iwi.eShopMaster.model.businessLogic.manager.impl;

import hska.iwi.eShopMaster.model.businessLogic.manager.UserManager;
import hska.iwi.eShopMaster.model.database.dataAccessObjects.UserDAO;
import hska.iwi.eShopMaster.model.domain.Role;
import hska.iwi.eShopMaster.model.domain.User;
import hska.iwi.eShopMaster.model.domain.UserDto;

/**
 * @author knad0001
 */
public class UserManagerImpl implements UserManager {

    private final UserDAO helper = new UserDAO();

    public User registerUser(String username, String firstname, String lastname, String password, String roletype) {
        UserDto user = new UserDto(username, firstname, lastname, password, roletype);
        return helper.createUser(user);
    }

    public Role getRoleByLevel(int level) {
        throw new UnsupportedOperationException();
    }

    public boolean doesUserAlreadyExist(String username) {
        return helper.doesUserAlreadyExist(username);
    }

    public boolean validate(User user) {
        if (user.getFirstname().isEmpty() || user.getPassword().isEmpty() || user.getRole() == null || user.getLastname() == null || user.getUsername() == null) {
            return false;
        }
        return true;
    }

}
