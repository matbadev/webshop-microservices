package hska.iwi.eShopMaster.model;

public class ApiConfig {

    private static final String SERVICE_BASE = "http://edge-service:8080";

    public static final String AUTH_ACCESS_TOKEN = SERVICE_BASE + "/auth/oauth/token";
    public static final String AUTH_ME = SERVICE_BASE + "/auth/me";

    private static final String SERVICE_INVENTORY = SERVICE_BASE + "/inventory-api";
    public static final String API_INVENTORY_PRODUCTS = SERVICE_INVENTORY + "/products";
    public static final String API_INVENTORY_CATEGORIES = SERVICE_INVENTORY + "/categories";

    private static final String SERVICE_USER = SERVICE_BASE + "/user-api";
    public static final String API_USERS = SERVICE_USER + "/users";

}
