package de.hska.iwi.vslab.webshopcorecategoryservice.category;

import de.hska.iwi.vslab.webshopcorecategoryservice.category.CategoryDto.NewCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class CategoryController {

    private final CategoryRepository categoryRepo;

    @Autowired
    public CategoryController(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @GetMapping("/categories")
    @RolesAllowed({"ROLE_USER"})
    public Iterable<Category> getCategories() {
        return categoryRepo.findAll();
    }

    @PostMapping("/categories")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Category> addCategory(@RequestBody @Valid NewCategoryDto category) {
        try {
            Category newCategory = categoryRepo.save(new Category(category.getName()));
            return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/categories/{categoryId}")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<Category> getCategory(@PathVariable Long categoryId) {
        Optional<Category> category = categoryRepo.findById(categoryId);
        return category.map((value) -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/categories/{categoryId}")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryRepo.deleteById(categoryId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
