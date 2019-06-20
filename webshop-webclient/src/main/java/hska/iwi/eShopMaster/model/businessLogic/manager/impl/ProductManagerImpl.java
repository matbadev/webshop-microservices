package hska.iwi.eShopMaster.model.businessLogic.manager.impl;

import hska.iwi.eShopMaster.model.businessLogic.manager.ProductManager;
import hska.iwi.eShopMaster.model.database.dataAccessObjects.CategoryDAO;
import hska.iwi.eShopMaster.model.database.dataAccessObjects.ProductDAO;
import hska.iwi.eShopMaster.model.domain.Category;
import hska.iwi.eShopMaster.model.domain.Product;
import hska.iwi.eShopMaster.model.domain.ProductDto;

import java.util.List;

public class ProductManagerImpl implements ProductManager {

    private final ProductDAO helper = new ProductDAO();
    private final CategoryDAO categoryHelper = new CategoryDAO();

    public List<Product> getProducts() {
        return helper.getProducts();
    }

    public List<Product> getProductsForSearchValues(String searchDescription,
                                                    Double searchMinPrice, Double searchMaxPrice) {
        return helper.getProductListByCriteria(searchDescription, searchMinPrice, searchMaxPrice);
    }

    public Product getProductById(int id) {
        return helper.getProductById(id);
    }

    public Product getProductByName(String name) {
        return helper.getProductByName(name);
    }

    public int addProduct(String name, double price, int categoryId, String details) {
        Category category = categoryHelper.getCategoryById(categoryId);
        ProductDto productDto = new ProductDto(name, price, details, category.getName());
        Product product = helper.createProduct(productDto);
        return product.getId();
    }

    public void deleteProductById(int id) {
        helper.deleteProductById(id);
    }

    public boolean deleteProductsByCategoryId(int categoryId) {
        throw new UnsupportedOperationException();
    }

}
