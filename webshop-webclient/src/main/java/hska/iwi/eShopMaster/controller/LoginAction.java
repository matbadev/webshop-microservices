package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.ApiConfig;
import hska.iwi.eShopMaster.model.domain.User;
import hska.iwi.eShopMaster.model.domain.UserAuthDetails;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public class LoginAction extends ActionSupport {

    private static final Logger LOGGER = Logger.getLogger(LoginAction.class.getSimpleName());

    private static final long serialVersionUID = -983183915002226000L;

    private String username = null;
    private String password = null;
    private String firstname;
    private String lastname;
    private String role;

    @Override
    public String execute() {
        OAuth2AccessToken accessToken;
        try {
            accessToken = loadAccessToken();
        } catch (OAuth2AccessDeniedException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof HttpClientErrorException) {
                HttpStatus status = ((HttpClientErrorException) cause).getStatusCode();
                if (status.equals(HttpStatus.UNAUTHORIZED)) {
                    addActionError(getText("error.username.wrong"));
                    return INPUT;
                }
            } else if (cause instanceof OAuth2Exception) {
                addActionError(getText("error.password.wrong"));
                return INPUT;
            }
            throw ex;
        }

        UserAuthDetails userAuthDetails = loadUserAuthDetails(accessToken);
        LOGGER.info("Received UserAuthDetails: " + userAuthDetails);

        Map<String, Object> session = ActionContext.getContext().getSession();
        session.put("webshop_user", userAuthDetails.getPrincipal());
        session.put("access_token", accessToken);
        session.put("message", "");

        User user = userAuthDetails.getPrincipal();
        firstname = user.getFirstname();
        lastname = user.getLastname();
        role = user.getRole().getType();

        return SUCCESS;
    }

    private OAuth2AccessToken loadAccessToken() {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setAccessTokenUri(ApiConfig.AUTH_ACCESS_TOKEN);
        resource.setClientId("webshop-webclient");
        resource.setClientSecret("secret");
        resource.setGrantType("password");
        resource.setUsername(username);
        resource.setPassword(password);

        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(resource);

        return oAuth2RestTemplate.getAccessToken();
    }

    private UserAuthDetails loadUserAuthDetails(OAuth2AccessToken accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getValue());
        HttpEntity entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserAuthDetails> detailsEntity = restTemplate.exchange(
                ApiConfig.AUTH_ME, HttpMethod.GET, entity, UserAuthDetails.class);
        return requireNonNull(detailsEntity.getBody());
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
