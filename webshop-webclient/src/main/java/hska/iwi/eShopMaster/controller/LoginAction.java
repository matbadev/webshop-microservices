package hska.iwi.eShopMaster.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.ApiConfig;
import hska.iwi.eShopMaster.model.domain.User;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginAction extends ActionSupport {

    private static final Logger logger = Logger.getLogger(LoginAction.class.getSimpleName());

    private OAuth2RestTemplate restTemplate = ApiConfig.getRestTemplate();

    private static final long serialVersionUID = -983183915002226000L;

    private String username = null;
    private String password = null;
    private String firstname;
    private String lastname;
    private String role;

    @Override
    public String execute() {
        ResourceOwnerPasswordResourceDetails resourceDetails = (ResourceOwnerPasswordResourceDetails) restTemplate.getResource();
        resourceDetails.setUsername(username);
        resourceDetails.setPassword(password);

        OAuth2AccessToken accessToken;

        try {
            accessToken = restTemplate.getAccessToken();
        } catch (Exception ex) {
            ex.printStackTrace();
            addActionError(getText("error.username.wrong"));
            return INPUT;
        }

        Map<String, Object> information = accessToken.getAdditionalInformation();
        String userString = (String)information.get("user");

        ObjectMapper mapper = new ObjectMapper();
        User user;
        try {
            user = mapper.readValue(userString, User.class);
        } catch (Exception ex) {
            logger.log(Level.WARNING, ex, () -> "");
            return INPUT;
        }

        logger.info("Read user: " + user);

        Map<String, Object> session = ActionContext.getContext().getSession();
        session.put("webshop_user", user);
        session.put("message", "");
        firstname = user.getFirstname();
        lastname = user.getLastname();
        role = user.getRole().getType();
        return SUCCESS;
    }

    @Override
    public void validate() {
        if (getUsername().length() == 0) {
            addActionError(getText("error.username.required"));
        }
        if (getPassword().length() == 0) {
            addActionError(getText("error.password.required"));
        }
    }

    public String getUsername() {
        return (this.username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return (this.password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
