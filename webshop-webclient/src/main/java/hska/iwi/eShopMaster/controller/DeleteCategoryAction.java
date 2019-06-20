package hska.iwi.eShopMaster.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import hska.iwi.eShopMaster.model.businessLogic.manager.CategoryManager;
import hska.iwi.eShopMaster.model.businessLogic.manager.impl.CategoryManagerImpl;
import hska.iwi.eShopMaster.model.domain.Category;
import hska.iwi.eShopMaster.model.domain.User;

import java.util.List;
import java.util.Map;

public class DeleteCategoryAction extends ActionSupport {

    private static final long serialVersionUID = 1254575994729199914L;

    private int catId;
    private List<Category> categories;

    public String execute() {
        String res = "input";

        Map<String, Object> session = ActionContext.getContext().getSession();
        User user = (User) session.get("webshop_user");

        if (user != null && (user.getRole().getType().equals("admin"))) {
            // Helper inserts new Category in DB:
            CategoryManager categoryManager = new CategoryManagerImpl();

            categoryManager.delCategoryById(catId);

            categories = categoryManager.getCategories();

            res = "success";
        }

        return res;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}
