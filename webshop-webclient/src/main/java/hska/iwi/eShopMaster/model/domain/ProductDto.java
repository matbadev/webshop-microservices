package hska.iwi.eShopMaster.model.domain;

public class ProductDto {

    private String name;
    private double price;
    private String details;
    private String category;

    public ProductDto() {
    }

    public ProductDto(String name, double price, String details, String category) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.category = category;
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
