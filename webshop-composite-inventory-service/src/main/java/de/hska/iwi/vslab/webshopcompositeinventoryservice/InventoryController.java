package de.hska.iwi.vslab.webshopcompositeinventoryservice;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/")
public class InventoryController {

    private final String productsUrl = "http://productservice:8080/products";

    // TODO: richtige URL zum category Service ergänzen
    private final String categoriesUrl = "http://categoryservice:8080/categories";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping(path = "/hello")
    public @ResponseBody
    String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello " + name;
    }

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

        // same bulky statement for the categories
        ResponseEntity<List<Category>> responseCat = restTemplate.exchange(
                categoriesUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Category>>() {
                }
        );
        List<Category> categories = responseCat.getBody();

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

        return products;
    }

}
