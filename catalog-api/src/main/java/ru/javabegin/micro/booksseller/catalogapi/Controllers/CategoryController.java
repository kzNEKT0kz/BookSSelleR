package ru.javabegin.micro.booksseller.catalogapi.Controllers;

import org.springframework.web.bind.annotation.*;
import ru.javabegin.micro.booksseller.catalogapi.Entities.Category;
import ru.javabegin.micro.booksseller.catalogapi.Services.CategoryService;

@RestController
@RequestMapping(path = "/catalog")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @PostMapping(path = "/addCatalog")
    public Category create(@RequestBody Category category) {
        return categoryService.create(category);
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
