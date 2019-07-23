package hska.iwi.eShopMaster.model.database.dataAccessObjects;

import hska.iwi.eShopMaster.model.domain.Category;
import hska.iwi.eShopMaster.model.domain.CategoryDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static hska.iwi.eShopMaster.NetworkUtils.buildBearerEntity;
import static hska.iwi.eShopMaster.model.ApiConfig.API_INVENTORY_CATEGORIES;
import static java.util.Objects.requireNonNull;

public class CategoryDAO {

    private RestTemplate restTemplate = new RestTemplate();

    public List<Category> getCategories() {
        ResponseEntity<Category[]> categoriesEntity = restTemplate.exchange(
                API_INVENTORY_CATEGORIES, HttpMethod.GET, buildBearerEntity(), Category[].class);
        return List.of(requireNonNull(categoriesEntity.getBody()));
    }

    public Category getCategoryById(int id) {
        ResponseEntity<Category> categoryEntity = restTemplate.exchange(
                API_INVENTORY_CATEGORIES + "/" + id, HttpMethod.GET, buildBearerEntity(), Category.class);
        return requireNonNull(categoryEntity.getBody());
    }

    public Category getCategoryByName(String name) {
        throw new UnsupportedOperationException();
    }

    public Category createCategory(CategoryDto categoryDto) {
        ResponseEntity<Category> categoryEntity = restTemplate.exchange(
                API_INVENTORY_CATEGORIES, HttpMethod.POST, buildBearerEntity(categoryDto), Category.class);
        return requireNonNull(categoryEntity.getBody());
    }

    public void deleteById(int id) {
        restTemplate.exchange(
                API_INVENTORY_CATEGORIES + "/" + id, HttpMethod.DELETE, buildBearerEntity(), Object.class);
    }

}
