package de.hska.iwi.vslab.webshopcompositeinventoryservice.model;

import javax.validation.constraints.NotEmpty;

public class CategoryDto {

    @NotEmpty
    private String name;

    public CategoryDto() {
    }

    public CategoryDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
