package hska.iwi.eShopMaster.model.database.dataAccessObjects;

import hska.iwi.eShopMaster.model.domain.Product;
import hska.iwi.eShopMaster.model.domain.ProductDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static hska.iwi.eShopMaster.NetworkUtils.buildBearerEntity;
import static hska.iwi.eShopMaster.model.ApiConfig.API_INVENTORY_PRODUCTS;
import static java.util.Objects.requireNonNull;

public class ProductDAO {

    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getSimpleName());

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Product> getProducts() {
        ResponseEntity<Product[]> productsEntity = restTemplate.exchange(
                API_INVENTORY_PRODUCTS, HttpMethod.GET, buildBearerEntity(), Product[].class);
        return List.of(requireNonNull(productsEntity.getBody()));
    }

    public Product getProductById(int id) {
        ResponseEntity<Product> productEntity = restTemplate.exchange(
                API_INVENTORY_PRODUCTS + "/" + id, HttpMethod.GET, buildBearerEntity(), Product.class);
        return requireNonNull(productEntity.getBody());
    }

    public Product getProductByName(String name) {
        throw new UnsupportedOperationException();
    }

    public List<Product> getProductListByCriteria(String text, Double minPrice, Double maxPrice) {
        LOGGER.info(String.format("[getProductListByCriteria] text='%s', minPrice='%s', maxPrice='%s'", text, minPrice, maxPrice));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(API_INVENTORY_PRODUCTS);
        if (text != null) uriBuilder.queryParam("text", text);
        if (minPrice != null) uriBuilder.queryParam("minPrice", minPrice);
        if (maxPrice != null) uriBuilder.queryParam("maxPrice", maxPrice);

        ResponseEntity<Product[]> productsEntity = restTemplate.exchange(
                uriBuilder.toUriString(), HttpMethod.GET, buildBearerEntity(), Product[].class);
        Product[] products = requireNonNull(productsEntity.getBody());
        LOGGER.info("Found " + products.length + " products: " + Arrays.deepToString(products));
        return List.of(products);
    }

    public Product createProduct(ProductDto productDto) {
        ResponseEntity<Product> productEntity = restTemplate.exchange(
                API_INVENTORY_PRODUCTS, HttpMethod.POST, buildBearerEntity(productDto), Product.class);
        return requireNonNull(productEntity.getBody());

    }

    public void deleteProductById(int id) {
        restTemplate.exchange(
                API_INVENTORY_PRODUCTS + "/" + id, HttpMethod.DELETE, buildBearerEntity(), Object.class);
    }

}
