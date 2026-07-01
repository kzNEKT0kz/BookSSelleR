package ru.javabegin.micro.booksseller.catalogapi.Controllers;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.javabegin.micro.booksseller.catalogapi.DTO.CategoryCreateRequest;
import ru.javabegin.micro.booksseller.catalogapi.Entities.Category;
import ru.javabegin.micro.booksseller.catalogapi.Services.CategoryService;

@RestController
@RequestMapping(path = "/catalog")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @PostMapping(path = "/addCatalog")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void create(@Valid @RequestBody CategoryCreateRequest category){
        categoryService.create(category);
    }

    @DeleteMapping(path = "/deleteCatalog/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    @PutMapping(path = "/updateCatalog")
    public Category update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    @GetMapping(path = "/test")
    public String test() {return "test";}
}
