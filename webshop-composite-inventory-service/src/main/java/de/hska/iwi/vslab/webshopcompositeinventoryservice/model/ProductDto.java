package de.hska.iwi.vslab.webshopcompositeinventoryservice.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

public class ProductDto {

    @NotEmpty
    private String name;

    @Positive
    private double price;

    @NotEmpty
    private String details;

    @NotEmpty
    private String category;

    public ProductDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
