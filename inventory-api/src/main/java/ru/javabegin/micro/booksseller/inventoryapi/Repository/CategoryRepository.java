package ru.javabegin.micro.booksseller.inventoryapi.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.javabegin.micro.booksseller.inventoryapi.Domain.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {

    @Query("{ 'name' : ?0 }")
    boolean findByName(String name);

}
