package de.hska.iwi.vslab.edgeserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hska.iwi.vslab.edgeserver.model.UserCore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        List<UserCore> users = authentication.getAuthorities().stream()
                .map((GrantedAuthority authority) -> (UserCore) authority)
                .collect(Collectors.toList());

        String userString;
        ObjectMapper mapper = new ObjectMapper();
        try {
            userString = mapper.writeValueAsString(users.get(0));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }

        Map<String, Object> authoritiesMap = Map.of("user", userString);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(authoritiesMap);
        return accessToken;
    }

}
