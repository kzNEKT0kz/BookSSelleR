package ru.javabegin.micro.booksseller.inventoryapi.Repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import ru.javabegin.micro.booksseller.inventoryapi.Domain.Book;

public interface InventoryRepository extends MongoRepository<Book, String> {
}
