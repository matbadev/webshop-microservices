package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.businessLogic.manager.ProductManager;
import hska.iwi.eShopMaster.model.businessLogic.manager.impl.ProductManagerImpl;
import hska.iwi.eShopMaster.model.domain.Product;
import hska.iwi.eShopMaster.model.domain.User;

import java.util.Map;

public class ProductDetailsAction extends ActionSupport {

    private static final long serialVersionUID = 7708747680872125699L;

    private User user;
    private int id;
    private String searchValue;
    private Integer searchMinPrice;
    private Integer searchMaxPrice;
    private Product product;

    public String execute() {
        String res = "input";

        Map<String, Object> session = ActionContext.getContext().getSession();
        user = (User) session.get("webshop_user");

        if (user != null) {
            ProductManager productManager = new ProductManagerImpl();
            product = productManager.getProductById(id);

            res = "success";
        }

        return res;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public Integer getSearchMinPrice() {
        return searchMinPrice;
    }

    public void setSearchMinPrice(Integer searchMinPrice) {
        this.searchMinPrice = searchMinPrice;
    }

    public Integer getSearchMaxPrice() {
        return searchMaxPrice;
    }

    public void setSearchMaxPrice(Integer searchMaxPrice) {
        this.searchMaxPrice = searchMaxPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
