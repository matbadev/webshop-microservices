package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.businessLogic.manager.CategoryManager;
import hska.iwi.eShopMaster.model.businessLogic.manager.ProductManager;
import hska.iwi.eShopMaster.model.businessLogic.manager.impl.CategoryManagerImpl;
import hska.iwi.eShopMaster.model.businessLogic.manager.impl.ProductManagerImpl;
import hska.iwi.eShopMaster.model.domain.Category;
import hska.iwi.eShopMaster.model.domain.Product;
import hska.iwi.eShopMaster.model.domain.User;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchAction extends ActionSupport {

    private static final long serialVersionUID = -6565401833074694229L;

    private String searchDescription = null;
    private String searchMinPrice;
    private String searchMaxPrice;

    private Double sMinPrice = null;
    private Double sMaxPrice = null;

    private User user;
    private List<Product> products;
    private List<Category> categories;

    public String execute() {
        String result = "input";

        // Get user:
        Map<String, Object> session = ActionContext.getContext().getSession();
        user = (User) session.get("webshop_user");
        ActionContext.getContext().setLocale(Locale.US);

        if (user != null) {
            // Search products and show results:
            ProductManager productManager = new ProductManagerImpl();
            if (!searchMinPrice.isEmpty()) {
                sMinPrice = Double.parseDouble(this.searchMinPrice);
            }
            if (!searchMaxPrice.isEmpty()) {
                sMaxPrice = Double.parseDouble(this.searchMaxPrice);
            }
            this.products = productManager.getProductsForSearchValues(this.searchDescription, sMinPrice, sMaxPrice);

            // Show all categories:
            CategoryManager categoryManager = new CategoryManagerImpl();
            this.categories = categoryManager.getCategories();
            result = "success";
        }

        return result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getSearchValue() {
        return searchDescription;
    }

    public void setSearchValue(String searchValue) {
        this.searchDescription = searchValue;
    }

    public String getSearchMinPrice() {
        return searchMinPrice;
    }

    public void setSearchMinPrice(String searchMinPrice) {
        this.searchMinPrice = searchMinPrice;
    }

    public String getSearchMaxPrice() {
        return searchMaxPrice;
    }

    public void setSearchMaxPrice(String searchMaxPrice) {
        this.searchMaxPrice = searchMaxPrice;
    }

}
