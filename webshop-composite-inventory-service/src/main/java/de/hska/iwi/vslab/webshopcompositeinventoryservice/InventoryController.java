package de.hska.iwi.vslab.webshopcompositeinventoryservice;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

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
    public @ResponseBody
    List<Product> getProducts(@RequestParam(required = false) String text,
                              @RequestParam(required = false) String minPrice,
                              @RequestParam(required = false) String maxPrice) {

        // pack params into the url
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(productsUrl)
                .queryParam("text", text)
                .queryParam("minPrice", minPrice)
                .queryParam("maxPrice", maxPrice);

        // bulky statement, needed to retrieve collection of products
        ResponseEntity<List<ProductDto>> responseProd = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProductDto>>() {
                }
        );
        List<ProductDto> incompleteProducts = responseProd.getBody();

        List<Category> categories = getCategories(); // REST call
        Map<Integer, Category> categoryMap = categories
                .stream()
                .collect(Collectors.toMap(Category::getId, category -> category, (a, b) -> b));

        Category defaultCategory = new Category();
        // TODO: change to suitable id and name
        defaultCategory.setId(-1);
        defaultCategory.setName("DefaultCategory");

        List<Product> products = incompleteProducts
                .stream()
                .map(productDto -> {
                    Product product = Product.fromDto(productDto);
                    product.setCategory(categoryMap.getOrDefault(productDto.getCategoryId(), defaultCategory));
                    return product;
                })
                .collect(Collectors.toList());

        productCache.clear();
        products.forEach(product -> productCache.put(product.getId(), product));

        return products;
    }

    public List<Product> getProductsCache() {
        return new ArrayList<>(productCache.values());
    }

    @HystrixCommand(fallbackMethod = "newProductCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @PostMapping(path = "/products")
    public ResponseEntity<Product> newProduct(@RequestBody ProductDto newProductDto) {
        // REST call
        List<Category> categories = getCategories();
        Optional<Category> categoryOptional = categories.stream()
                .filter(category -> category.getName().equals(newProductDto.getName()))
                .findFirst();

        Category category;

        // set the category id in the dto, so that the product gets correctly created
        if (categoryOptional.isPresent()) {
            category = categoryOptional.get();
            newProductDto.setCategoryId(category.getId());
        } else {
            // create category if it did not exist
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setName(newProductDto.getCategory());

            // REST call
            ResponseEntity<Category> categoryResponseEntity = postCategories(categoryDto);

            if (!categoryResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else {
                category = categoryResponseEntity.getBody();
                newProductDto.setCategoryId(category.getId());
            }
        }

        // create the product
        // REST call
        ResponseEntity<Product> productResponseEntity = restTemplate.postForEntity(productsUrl, newProductDto, Product.class);

        if (!productResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // put the category object into the newly created product
        Product product = productResponseEntity.getBody();
        product.setCategory(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    public ResponseEntity<Product> newProductCache(@RequestBody ProductDto newProductDto) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @HystrixCommand(fallbackMethod = "getProductCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @GetMapping(path = "/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable int productId) {
        ResponseEntity<ProductDto> productDtoResponseEntity = restTemplate.getForEntity(productsUrl + "/" + productId, ProductDto.class);
        if (!productDtoResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ProductDto productDto = productDtoResponseEntity.getBody();

        ResponseEntity<Category> categoryResponseEntity = getCategory(productDto.getCategoryId());
        if (!categoryResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Product.fromDto(productDto));
        }

        Category category = categoryResponseEntity.getBody();

        Product product = Product.fromDto(productDto);
        product.setCategory(category);

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
        List<Category> categories =  restTemplate.exchange(
                categoriesUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Category>>() {
                }
        ).getBody();

        categoryCache.clear();
        categories.forEach(category -> categoryCache.put(category.getId(), category));

        return categories;
    }

    public List<Category> getCategoriesCache() {
        return new ArrayList<>(categoryCache.values());
    }

    @HystrixCommand(fallbackMethod = "postCategoriesCache", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
    })
    @PostMapping(path = "/categories")
    public ResponseEntity<Category> postCategories(@RequestBody CategoryDto newCategory) {
        return restTemplate.postForEntity(categoriesUrl, newCategory, Category.class);
    }

    public ResponseEntity<Category> postCategoriesCache(@RequestBody CategoryDto newCategory){
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