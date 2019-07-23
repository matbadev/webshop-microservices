package de.hska.iwi.vslab.webshopcompositeinventoryservice.model;

public class Product {

    private int id;
    private String name;
    private double price;
    private String details;
    private Category category;

    public Product() {
    }

    public Product(int id, String name, double price, String details, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.details = details;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
