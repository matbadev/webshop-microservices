package hska.iwi.eShopMaster.model;

import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

public class ApiConfig {

    // TODO Move webclient to own Docker network and adjust URL to localhost:9255
    private static final String SERVICE_BASE = "http://edge-service:8080";

    private static final String SERVICE_INVENTORY = SERVICE_BASE + "/inventory-api";
    public static final String API_INVENTORY_PRODUCTS = SERVICE_INVENTORY + "/products";
    public static final String API_INVENTORY_CATEGORIES = SERVICE_INVENTORY + "/categories";

    private static final String SERVICE_USER = SERVICE_BASE + "/user-api";
    public static final String API_USERS = SERVICE_USER + "/users";


    private static OAuth2RestTemplate restTemplate;

    public static synchronized OAuth2RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            resetRestTemplate();
        }
        return restTemplate;
    }

    public static void resetRestTemplate() {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setAccessTokenUri("http://edge-service:8080/oauth/token");
        resource.setClientId("webshop-webclient");
        resource.setClientSecret("secret");
        resource.setGrantType("password");
        restTemplate = new OAuth2RestTemplate(resource);
    }

}
