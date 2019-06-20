package hska.iwi.eShopMaster.model.database.dataAccessObjects;

import hska.iwi.eShopMaster.model.domain.Product;
import hska.iwi.eShopMaster.model.domain.ProductDto;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static hska.iwi.eShopMaster.model.ApiConfig.API_INVENTORY_PRODUCTS;
import static java.util.Objects.requireNonNull;

public class ProductDAO {

    private static final Logger logger = Logger.getLogger(ProductDAO.class.getSimpleName());

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Product> getProducts() {
        Product[] products = requireNonNull(restTemplate.getForObject(API_INVENTORY_PRODUCTS, Product[].class));
        return List.of(products);
    }

    public Product getProductById(int id) {
        return restTemplate.getForObject(API_INVENTORY_PRODUCTS + "/" + id, Product.class);
    }

    public Product getProductByName(String name) {
        throw new UnsupportedOperationException();
    }

    public List<Product> getProductListByCriteria(String text, Double minPrice, Double maxPrice) {
        logger.info(String.format("[getProductListByCriteria] text='%s', minPrice='%s', maxPrice='%s'", text, minPrice, maxPrice));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(API_INVENTORY_PRODUCTS);
        if (text != null) uriBuilder.queryParam("text", text);
        if (minPrice != null) uriBuilder.queryParam("minPrice", minPrice);
        if (maxPrice != null) uriBuilder.queryParam("maxPrice", maxPrice);

        Product[] products = requireNonNull(restTemplate.getForObject(uriBuilder.toUriString(), Product[].class));
        logger.info("Found " + products.length + " products: " + Arrays.deepToString(products));
        return List.of(products);
    }

    public Product createProduct(ProductDto productDto) {
        return restTemplate.postForObject(API_INVENTORY_PRODUCTS, productDto, Product.class);
    }

    public void deleteProductById(int id) {
        restTemplate.delete(API_INVENTORY_PRODUCTS + "/" + id);
    }

}
