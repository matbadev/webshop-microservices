package de.hska.iwi.vslab.webshopcoreproductservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping(path = "/")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping(path = "/products")
    public @ResponseBody
    Iterable<Product> getProducts(@RequestParam(defaultValue = "") String text,
                                  @RequestParam(defaultValue = "-1e20") double minPrice,
                                  @RequestParam(defaultValue = "1e20") double maxPrice) {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .filter(product -> (product.getName().contains(text) || product.getDetails().contains(text))
                        && product.getPrice() >= minPrice
                        && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/products")
    public @ResponseBody
    Product newProduct(@RequestBody Product newProduct) {
        return productRepository.save(newProduct);
    }

    @GetMapping(path = "/products/{productId}")
    public @ResponseBody
    Product getProductById(@PathVariable int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "/products/{productId}")
    public ResponseEntity<Product> deleteProductById(@PathVariable int productId) {
        if (productRepository.findById(productId).isPresent()) {
            productRepository.deleteById(productId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
