package ru.javabegin.micro.booksseller.inventoryapi.Services;

import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.inventoryapi.Domain.Category;
import ru.javabegin.micro.booksseller.inventoryapi.Repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void create (Category category) {
        categoryRepository.save(category);
    }

}
