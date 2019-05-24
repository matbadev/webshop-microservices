package de.hska.iwi.vslab.webshopcoreproductservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
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
    public ResponseEntity<Product> newProduct(@RequestBody Product newProduct) {
        try {
            Product product = productRepository.save(newProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(path = "/products/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable int productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            return ResponseEntity.ok(productOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
