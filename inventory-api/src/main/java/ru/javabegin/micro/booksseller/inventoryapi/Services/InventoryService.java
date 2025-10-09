package ru.javabegin.micro.booksseller.inventoryapi.Services;

import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.inventoryapi.Collections.Book;
import ru.javabegin.micro.booksseller.inventoryapi.Repository.CategoryRepository;
import ru.javabegin.micro.booksseller.inventoryapi.Repository.InventoryRepository;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;

    public InventoryService(InventoryRepository inventoryRepository , CategoryRepository categoryRepository) {
        this.inventoryRepository = inventoryRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addBook(Book book) {

        if(!CheckExistCategory(book.getGenre())){

             throw new  IllegalArgumentException("Genre does not exist");
        }

         inventoryRepository.save(book);

    }

    public void removeBook(String id) {

        inventoryRepository.deleteById(id);
    }

    public Book updateBook(Book book) {

        if(inventoryRepository.existsById(book.getId())){

            Book currentBook = new Book();
            currentBook.setTitle(book.getTitle());
            currentBook.setAuthor(book.getAuthor());
            currentBook.setDescription(book.getDescription());
            currentBook.setId(book.getId());
            book.setAvailable(book.getStockQuantity() > 0);


            return inventoryRepository.save(book);
        }

        throw new IllegalArgumentException("Book not found");
    }

    public List<Book> getAllBooks() {
        return inventoryRepository.findAll();
    }


    public boolean CheckExistCategory(String category) {

        if(categoryRepository.findByName(category)){
            return true;
        }
        return false;
    }

}
