package hska.iwi.eShopMaster.model.businessLogic.manager.impl;

import hska.iwi.eShopMaster.model.businessLogic.manager.CategoryManager;
import hska.iwi.eShopMaster.model.database.dataAccessObjects.CategoryDAO;
import hska.iwi.eShopMaster.model.domain.Category;
import hska.iwi.eShopMaster.model.domain.CategoryDto;

import java.util.List;

public class CategoryManagerImpl implements CategoryManager {

    private final CategoryDAO helper = new CategoryDAO();

    public List<Category> getCategories() {
        return helper.getCategories();
    }

    public Category getCategory(int id) {
        return helper.getCategoryById(id);
    }

    public Category getCategoryByName(String name) {
        return helper.getCategoryByName(name);
    }

    public void addCategory(String name) {
        CategoryDto cat = new CategoryDto(name);
        helper.createCategory(cat);
    }

    public void delCategory(Category cat) {
        helper.deleteById(cat.getId());
    }

    public void delCategoryById(int id) {
        helper.deleteById(id);
    }

}
