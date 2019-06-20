package de.hska.iwi.vslab.webshopcompositeinventoryservice;

public class ProductCore {

    private int id;
    private String name;
    private double price;
    private String details;
    private int categoryId;

    public ProductCore() {
    }

    public ProductCore(String name, double price, String details, int categoryId) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.categoryId = categoryId;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
