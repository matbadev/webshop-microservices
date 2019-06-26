package de.hska.iwi.vslab.edgeserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

public class UserCore implements GrantedAuthority {

    private int id;

    private String username;

    private String firstname;

    private String lastname;

    private String password;

    private RoleCore role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleCore getRole() {
        return role;
    }

    public void setRole(RoleCore role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserCore{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    @JsonIgnore
    @Override
    public String getAuthority() {
        return role.getType();
    }

}
