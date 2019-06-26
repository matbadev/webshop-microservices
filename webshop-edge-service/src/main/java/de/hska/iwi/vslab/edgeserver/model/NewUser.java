package de.hska.iwi.vslab.edgeserver.model;

import javax.validation.constraints.NotEmpty;

public class NewUser {

    @NotEmpty
    public String username;

    @NotEmpty
    public String firstname;

    @NotEmpty
    public String lastname;

    @NotEmpty
    public String password;

    @NotEmpty
    public String roletype;

}
