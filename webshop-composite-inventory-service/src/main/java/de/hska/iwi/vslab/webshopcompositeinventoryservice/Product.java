package de.hska.iwi.vslab.webshopcompositeinventoryservice;

public class Product {

    private int id;
    private String name;
    private double price;
    private String details;
    private Category category;

    public static Product fromDto(ProductDto productDto) {
        Product newProduct = new Product();
        newProduct.setId(productDto.getId());
        newProduct.setName(productDto.getName());
        newProduct.setPrice(productDto.getPrice());
        newProduct.setDetails(productDto.getDetails());
        return newProduct;
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
