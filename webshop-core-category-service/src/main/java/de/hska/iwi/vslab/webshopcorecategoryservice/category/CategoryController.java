package de.hska.iwi.vslab.webshopcorecategoryservice.category;

import de.hska.iwi.vslab.webshopcorecategoryservice.category.CategoryDto.NewCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RestController
@RequestMapping(path = "/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepo;

    @GetMapping
    public ResponseEntity<Iterable<Category>> getCategories() {
        Iterable<Category> allCategories = categoryRepo.findAll();
        return new ResponseEntity<>(allCategories, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> addCategory(@RequestBody NewCategoryDto category){
        try {
            categoryRepo.save(new Category(category.getName()));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(path = "/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable Long categoryId) {

        Optional<Category> category = categoryRepo.findById(categoryId);

        if (category.isPresent()) {
            return ResponseEntity.ok().body(category.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping(path = "/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {

        try {
            categoryRepo.deleteById(categoryId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
