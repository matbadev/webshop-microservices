package de.hska.iwi.vslab.webshopcompositeinventoryservice;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Controller
@EnableHystrix
@RequestMapping(path = "/")
public class InventoryController {

    private final String productsUrl = "http://product-service:8080/products";
    private final String categoriesUrl = "http://category-service:8080/categories";

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<Integer, Category> categoryCache = new HashMap<>();
    private final Map<Integer, Product> productCache = new HashMap<>();

    @HystrixCommand(fallbackMethod = "getProductsCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/products")
    @ResponseBody
    public List<Product> getProducts(@RequestParam(defaultValue = "") String text,
                                     @RequestParam(defaultValue = "-1e20") Double minPrice,
                                     @RequestParam(defaultValue = "1e20") Double maxPrice) {
        // bulky statement, needed to retrieve collection of products
        ProductCore[] responseProd = requireNonNull(restTemplate.getForObject(productsUrl, ProductCore[].class));
        List<ProductCore> incompleteProducts = Arrays.stream(responseProd)
                .filter(product -> (product.getName().contains(text) || product.getDetails().contains(text))
                        && product.getPrice() >= minPrice
                        && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());

        List<Category> categories = getCategories(); // REST call
        Map<Integer, Category> categoryMap = categories
                .stream()
                .collect(Collectors.toMap(Category::getId, category -> category, (a, b) -> b));

        List<Product> products = incompleteProducts.stream()
                .map((ProductCore productCore) -> {
                    Category category = categoryMap.get(productCore.getCategoryId());
                    return new Product(productCore.getId(), productCore.getName(), productCore.getPrice(), productCore.getDetails(), category);
                })
                .collect(Collectors.toList());

        productCache.clear();
        products.forEach(product -> productCache.put(product.getId(), product));

        return products;
    }

    public List<Product> getProductsCache(String text, Double minPrice, Double maxPrice) {
        return productCache.values()
                .stream()
                .filter(product -> (product.getName().contains(text) || product.getDetails().contains(text))
                        && product.getPrice() >= minPrice
                        && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    @HystrixCommand(fallbackMethod = "newProductCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @PostMapping(path = "/products")
    public ResponseEntity<Product> newProduct(@RequestBody ProductDto newProductDto) {
        String categoryName = newProductDto.getCategory();
        Optional<Category> categoryOptional = getCategories().stream()
                .filter(category -> category.getName().equals(categoryName))
                .findFirst();

        Category category;

        if (categoryOptional.isPresent()) {
            category = categoryOptional.get();
        } else {
            // create category if it did not exist
            CategoryDto categoryDto = new CategoryDto(categoryName);

            // REST call
            ResponseEntity<Category> categoryResponseEntity = createCategory(categoryDto);

            if (!categoryResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            category = categoryResponseEntity.getBody();
        }

        ProductCore newProductCore = new ProductCore(newProductDto.getName(), newProductDto.getPrice(), newProductDto.getDetails(), category.getId());

        // create the product
        // REST call
        ResponseEntity<ProductCore> productResponseEntity = restTemplate.postForEntity(productsUrl, newProductCore, ProductCore.class);

        if (!productResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // put the category object into the newly created product
        ProductCore createdProductCore = productResponseEntity.getBody();
        Product createdProduct = new Product(createdProductCore.getId(), createdProductCore.getName(), createdProductCore.getPrice(), createdProductCore.getDetails(), category);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    public ResponseEntity<Product> newProductCache(@RequestBody ProductDto newProductDto) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @HystrixCommand(fallbackMethod = "getProductCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable int productId) {
        ResponseEntity<ProductCore> productDtoResponseEntity = restTemplate.getForEntity(productsUrl + "/" + productId, ProductCore.class);
        if (!productDtoResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ProductCore productCore = productDtoResponseEntity.getBody();

        ResponseEntity<Category> categoryResponseEntity = getCategory(productCore.getCategoryId());
        if (!categoryResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Category category = categoryResponseEntity.getBody();

        Product product = new Product(productCore.getId(), productCore.getName(), productCore.getPrice(), productCore.getDetails(), category);

        productCache.putIfAbsent(productId, product);

        return ResponseEntity.ok(product);
    }

    public ResponseEntity<Product> getProductCache(@PathVariable int productId) {
        try {
            Product product = productCache.get(productId);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @HystrixCommand(fallbackMethod = "deleteProductCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @DeleteMapping(path = "/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int productId) {
        // TODO: decide if orphaned categories should be also removed
        try {
            restTemplate.delete(productsUrl + "/" + productId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<Void> deleteProductCache(@PathVariable int productId) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @HystrixCommand(fallbackMethod = "getCategoriesCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/categories")
    public @ResponseBody
    List<Category> getCategories() {
        // same bulky statement to get collection of categories
        Category[] categoriesArray = requireNonNull(restTemplate.getForObject(categoriesUrl, Category[].class));
        List<Category> categories = List.of(categoriesArray);

        categoryCache.clear();
        categories.forEach(category -> categoryCache.put(category.getId(), category));

        return categories;
    }

    public List<Category> getCategoriesCache() {
        return new ArrayList<>(categoryCache.values());
    }

    @HystrixCommand(fallbackMethod = "createCategoryCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @PostMapping(path = "/categories")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto newCategory) {
        return restTemplate.postForEntity(categoriesUrl, newCategory, Category.class);
    }

    public ResponseEntity<Category> createCategoryCache(@RequestBody CategoryDto newCategory) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @HystrixCommand(fallbackMethod = "getCategoryCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/categories/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable long categoryId) {
        try {
            Category category = restTemplate.getForObject(categoriesUrl + "/" + categoryId, Category.class);
            categoryCache.putIfAbsent((int) categoryId, category);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public ResponseEntity<Category> getCategoryCache(@PathVariable long categoryId) {
        try {
            Category category = categoryCache.get(categoryId);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @HystrixCommand(fallbackMethod = "deleteCategoryCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @DeleteMapping(path = "/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long categoryId) {
        try {
            restTemplate.delete(categoriesUrl + "/" + categoryId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<Void> deleteCategoryCache(@PathVariable long categoryId) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

}
