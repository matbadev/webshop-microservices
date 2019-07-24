package de.hska.iwi.vslab.webshopcorecategoryservice.category.CategoryDto;

import javax.validation.constraints.NotEmpty;

public class NewCategoryDto {

    @NotEmpty
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
