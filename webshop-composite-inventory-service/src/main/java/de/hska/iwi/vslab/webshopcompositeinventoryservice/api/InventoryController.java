package de.hska.iwi.vslab.webshopcompositeinventoryservice.api;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import de.hska.iwi.vslab.webshopcompositeinventoryservice.model.Category;
import de.hska.iwi.vslab.webshopcompositeinventoryservice.model.CategoryDto;
import de.hska.iwi.vslab.webshopcompositeinventoryservice.model.Product;
import de.hska.iwi.vslab.webshopcompositeinventoryservice.model.ProductCore;
import de.hska.iwi.vslab.webshopcompositeinventoryservice.model.ProductDto;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@RestController
@EnableHystrix
@RequestMapping("/")
public class InventoryController {

    private static final Logger LOGGER = Logger.getLogger(InventoryController.class.getSimpleName());

    private final String productsUrl = "http://product-service:8080/products";
    private final String categoriesUrl = "http://category-service:8080/categories";

    private final RestTemplate restTemplate = new RestTemplate();

    private final ConcurrentMap<Integer, Category> categoryCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Product> productCache = new ConcurrentHashMap<>();

    @HystrixCommand(fallbackMethod = "getProductsCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/products")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<List<Product>> getProducts(@RequestParam(defaultValue = "") String text,
                                                     @RequestParam(defaultValue = "-1e20") Double minPrice,
                                                     @RequestParam(defaultValue = "1e20") Double maxPrice,
                                                     OAuth2Authentication auth) {
        ResponseEntity<ProductCore[]> productCoresEntity;
        try {
            productCoresEntity = restTemplate.exchange(productsUrl, HttpMethod.GET, buildHttpEntity(auth), ProductCore[].class);
        } catch (OAuth2Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ProductCore> incompleteProducts = Arrays.stream(requireNonNull(productCoresEntity.getBody()))
                .filter(product -> (product.getName().contains(text) || product.getDetails().contains(text))
                        && product.getPrice() >= minPrice
                        && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());

        ResponseEntity<List<Category>> categoriesEntity = getCategories(auth);
        if (!isSuccessful(categoriesEntity)) {
            return ResponseEntity.status(categoriesEntity.getStatusCode()).build();
        }

        List<Category> categories = requireNonNull(categoriesEntity.getBody());
        Map<Integer, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, category -> category, (a, b) -> b));

        List<Product> products = incompleteProducts.stream()
                .map((ProductCore productCore) -> {
                    Category category = categoryMap.get(productCore.getCategoryId());
                    return new Product(productCore.getId(), productCore.getName(), productCore.getPrice(), productCore.getDetails(), category);
                })
                .collect(Collectors.toList());

        productCache.clear();
        products.forEach(product -> productCache.put(product.getId(), product));

        return ResponseEntity.ok(products);
    }

    @SuppressWarnings("unused")
    public ResponseEntity<List<Product>> getProductsCache(String text, Double minPrice, Double maxPrice, OAuth2Authentication auth) {
        List<Product> products = productCache.values()
                .stream()
                .filter(product -> (product.getName().contains(text) || product.getDetails().contains(text))
                        && product.getPrice() >= minPrice
                        && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @HystrixCommand(fallbackMethod = "newProductCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @PostMapping(path = "/products")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Product> newProduct(@RequestBody ProductDto newProductDto, OAuth2Authentication auth) {
        ResponseEntity<Category> categoryEntity = getOrCreateCategory(newProductDto.getCategory(), auth);
        if (!isSuccessful(categoryEntity)) {
            return ResponseEntity.status(categoryEntity.getStatusCode()).build();
        }
        Category category = requireNonNull(categoryEntity.getBody());

        ProductCore newProductCore = new ProductCore(newProductDto.getName(), newProductDto.getPrice(), newProductDto.getDetails(), category.getId());
        ResponseEntity<ProductCore> productCoreEntity = restTemplate.postForEntity(productsUrl, buildHttpEntity(auth, newProductCore), ProductCore.class);
        if (!isSuccessful(productCoreEntity)) {
            return ResponseEntity.status(productCoreEntity.getStatusCode()).build();
        }
        ProductCore productCore = requireNonNull(productCoreEntity.getBody());

        Product product = new Product(productCore.getId(), productCore.getName(), productCore.getPrice(), productCore.getDetails(), category);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    private ResponseEntity<Category> getOrCreateCategory(String categoryName, OAuth2Authentication auth) {
        ResponseEntity<List<Category>> categoriesEntity;
        try {
            categoriesEntity = getCategories(auth);
        } catch (OAuth2Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Category> categories = requireNonNull(categoriesEntity.getBody());

        Optional<Category> categoryOptional = categories.stream()
                .filter(category -> category.getName().equals(categoryName))
                .findFirst();

        return categoryOptional.map(ResponseEntity::ok)
                .orElseGet(() -> createCategory(new CategoryDto(categoryName), auth));
    }

    @SuppressWarnings("unused")
    public ResponseEntity<Product> newProductCache(@RequestBody ProductDto newProductDto, OAuth2Authentication auth) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @HystrixCommand(fallbackMethod = "getProductCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/products/{productId}")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId, OAuth2Authentication auth) {
        ResponseEntity<ProductCore> productCoreEntity;
        try {
            productCoreEntity = restTemplate.exchange(productsUrl + "/" + productId, HttpMethod.GET, buildHttpEntity(auth), ProductCore.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.notFound().build();
        } catch (OAuth2Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ProductCore productCore = requireNonNull(productCoreEntity.getBody());

        ResponseEntity<Category> categoryEntity = getCategory(productCore.getCategoryId(), auth);
        HttpStatus categoryStatus = categoryEntity.getStatusCode();
        if (categoryStatus != HttpStatus.OK) {
            if (categoryStatus == HttpStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            } else if (categoryStatus == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                throw new RuntimeException();
            }
        }
        Category category = categoryEntity.getBody();

        Product product = new Product(productCore.getId(), productCore.getName(), productCore.getPrice(), productCore.getDetails(), category);
        productCache.putIfAbsent(productId, product);
        return ResponseEntity.ok(product);
    }

    @SuppressWarnings("unused")
    public ResponseEntity<Product> getProductCache(@PathVariable Integer productId, OAuth2Authentication auth) {
        Product product = productCache.get(productId);
        if (product == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(product);
    }

    @HystrixCommand(fallbackMethod = "deleteProductCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @DeleteMapping(path = "/products/{productId}")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity deleteProduct(@PathVariable Integer productId, OAuth2Authentication auth) {
        try {
            restTemplate.exchange(productsUrl + "/" + productId, HttpMethod.DELETE, buildHttpEntity(auth), Object.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.notFound().build();
        } catch (OAuth2Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @SuppressWarnings("unused")
    public ResponseEntity deleteProductCache(@PathVariable Integer productId, OAuth2Authentication auth) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @HystrixCommand(fallbackMethod = "getCategoriesCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/categories")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<List<Category>> getCategories(OAuth2Authentication auth) {
        ResponseEntity<Category[]> categoriesEntity;
        try {
            categoriesEntity = restTemplate.exchange(categoriesUrl, HttpMethod.GET, buildHttpEntity(auth), Category[].class);
        } catch (OAuth2Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Category> categories = List.of(requireNonNull(categoriesEntity.getBody()));
        categoryCache.clear();
        categories.forEach(category -> categoryCache.put(category.getId(), category));
        return ResponseEntity.ok(categories);
    }

    @SuppressWarnings("unused")
    public ResponseEntity<List<Category>> getCategoriesCache(OAuth2Authentication auth) {
        return ResponseEntity.ok(List.copyOf(categoryCache.values()));
    }

    @HystrixCommand(fallbackMethod = "createCategoryCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @PostMapping(path = "/categories")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto newCategory, OAuth2Authentication auth) {
        try {
            return restTemplate.postForEntity(categoriesUrl, buildHttpEntity(auth, newCategory), Category.class);
        } catch (HttpClientErrorException.BadRequest ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (OAuth2Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @SuppressWarnings("unused")
    public ResponseEntity<Category> createCategoryCache(@RequestBody CategoryDto newCategory, OAuth2Authentication auth) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @HystrixCommand(fallbackMethod = "getCategoryCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/categories/{categoryId}")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<Category> getCategory(@PathVariable Integer categoryId, OAuth2Authentication auth) {
        ResponseEntity<Category> categoryEntity;
        try {
            categoryEntity = restTemplate.exchange(categoriesUrl + "/" + categoryId, HttpMethod.GET, buildHttpEntity(auth), Category.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.notFound().build();
        } catch (OAuth2Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Category category = requireNonNull(categoryEntity.getBody());
        categoryCache.putIfAbsent(categoryId, category);
        return ResponseEntity.ok(category);
    }

    @SuppressWarnings("unused")
    public ResponseEntity<Category> getCategoryCache(@PathVariable Integer categoryId, OAuth2Authentication auth) {
        Category category = categoryCache.get(categoryId);
        if (category == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(category);
    }

    @HystrixCommand(fallbackMethod = "deleteCategoryCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @DeleteMapping(path = "/categories/{categoryId}")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity deleteCategory(@PathVariable Integer categoryId, OAuth2Authentication auth) {
        try {
            restTemplate.exchange(categoriesUrl + "/" + categoryId, HttpMethod.DELETE, buildHttpEntity(auth), Object.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.notFound().build();
        } catch (OAuth2Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @SuppressWarnings("unused")
    public ResponseEntity deleteCategoryCache(@PathVariable Integer categoryId, OAuth2Authentication auth) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static <T> boolean isSuccessful(ResponseEntity<T> responseEntity) {
        int status = responseEntity.getStatusCodeValue();
        return status >= 200 && status < 300;
    }

    private HttpEntity buildHttpEntity(OAuth2Authentication auth) {
        return buildHttpEntity(auth, null);
    }

    private <T> HttpEntity<T> buildHttpEntity(OAuth2Authentication auth, @Nullable T body) {
        final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
        LOGGER.info("Token is: " + details.getTokenValue());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(details.getTokenValue());
        return new HttpEntity<>(body, headers);
    }

}
