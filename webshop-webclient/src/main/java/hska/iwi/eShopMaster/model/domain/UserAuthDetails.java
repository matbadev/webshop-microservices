package hska.iwi.eShopMaster.model.domain;

import java.util.List;

public class UserAuthDetails {

    private List<Authority> authorities;
    private User principal;

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public User getPrincipal() {
        return principal;
    }

    public void setPrincipal(User principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return "UserAuthDetails{" +
                "authorities=" + authorities +
                ", principal=" + principal +
                '}';
    }

}
