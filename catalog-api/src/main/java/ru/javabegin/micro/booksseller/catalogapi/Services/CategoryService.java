package ru.javabegin.micro.booksseller.catalogapi.Services;

import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.catalogapi.Entities.Category;
import ru.javabegin.micro.booksseller.catalogapi.Repositories.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        if(categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        }
        throw new IllegalStateException("Category already does not exist");
    }

    public Category update(Category category) {
        if(categoryRepository.existsById(category.getId())){
            Category currentCategory = new Category();
            currentCategory.setName(category.getName());
            currentCategory.setParent(category.getParent());
            currentCategory.setSubcategory(category.getSubcategory());
            return categoryRepository.save(currentCategory);
        }
        throw new IllegalStateException("Category does not exist");
    }

}
