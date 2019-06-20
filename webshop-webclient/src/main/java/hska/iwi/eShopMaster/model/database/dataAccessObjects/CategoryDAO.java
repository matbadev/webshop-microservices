package hska.iwi.eShopMaster.model.database.dataAccessObjects;

import hska.iwi.eShopMaster.model.domain.Category;
import hska.iwi.eShopMaster.model.domain.CategoryDto;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static hska.iwi.eShopMaster.model.ApiConfig.API_INVENTORY_CATEGORIES;
import static java.util.Objects.requireNonNull;

public class CategoryDAO {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Category> getCategories() {
        Category[] categories = requireNonNull(restTemplate.getForObject(API_INVENTORY_CATEGORIES, Category[].class));
        return List.of(categories);
    }

    public Category getCategoryById(int id) {
        return restTemplate.getForObject(API_INVENTORY_CATEGORIES + "/" + id, Category.class);
    }

    public Category getCategoryByName(String name) {
        throw new UnsupportedOperationException();
    }

    public Category createCategory(CategoryDto categoryDto) {
        return restTemplate.postForObject(API_INVENTORY_CATEGORIES, categoryDto, Category.class);
    }

    public void deleteById(int id) {
        restTemplate.delete(API_INVENTORY_CATEGORIES + "/" + id);
    }

}
