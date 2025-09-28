package ru.javabegin.micro.booksseller.inventoryapi.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.javabegin.micro.booksseller.inventoryapi.Collections.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
}
