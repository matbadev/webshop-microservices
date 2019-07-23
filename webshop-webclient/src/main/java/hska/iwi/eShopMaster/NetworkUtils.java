package hska.iwi.eShopMaster;

import com.opensymphony.xwork2.ActionContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Map;

public class NetworkUtils {

    public static <T> HttpEntity<T> buildBearerEntity() {
        return buildBearerEntity(null);
    }

    public static <T> HttpEntity<T> buildBearerEntity(T body) {
        Map<String, Object> session = ActionContext.getContext().getSession();
        OAuth2AccessToken accessToken = (OAuth2AccessToken) session.get("access_token");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getValue());
        return new HttpEntity<>(body, headers);
    }

}
