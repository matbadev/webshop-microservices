package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.businessLogic.manager.ProductManager;
import hska.iwi.eShopMaster.model.businessLogic.manager.impl.ProductManagerImpl;
import hska.iwi.eShopMaster.model.domain.Product;
import hska.iwi.eShopMaster.model.domain.User;

import java.util.List;
import java.util.Map;

public class ListAllProductsAction extends ActionSupport {

    private static final long serialVersionUID = -94109228677381902L;

    User user;
    private List<Product> products;

    public String execute() {
        String result = "input";

        Map<String, Object> session = ActionContext.getContext().getSession();
        user = (User) session.get("webshop_user");

        if (user != null) {
            System.out.println("list all products!");
            ProductManager productManager = new ProductManagerImpl();
            this.products = productManager.getProducts();
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

}
