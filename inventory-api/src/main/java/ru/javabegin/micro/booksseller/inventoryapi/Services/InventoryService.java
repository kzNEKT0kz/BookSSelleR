package ru.javabegin.micro.booksseller.inventoryapi.Services;

import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.inventoryapi.Domain.Book;
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


    public void DecreaseStockBooks (String id, int amount) {
        if(inventoryRepository.existsById(id)){
            Book book = inventoryRepository.findById(id).get();
            if(book.getStockQuantity() >= amount){
                book.setStockQuantity(book.getStockQuantity() - amount);
            }
        }
    }




    public void RentBooks(String id, int amount) {

        if(inventoryRepository.existsById(id)){


            Book book = inventoryRepository.findById(id).get();
            if(book.getFreeQuantity() > amount){
                book.setFreeQuantity(book.getFreeQuantity() - amount);
                book.setRentedQuantity(book.getRentedQuantity() + amount);
                inventoryRepository.save(book);
            }
            else throw new IllegalArgumentException("Out of stock");

        }
        else throw new IllegalArgumentException("Book not found");
    }

    public void ReturnRentBooks(String id, int amount) {
        if(inventoryRepository.existsById(id)){
            Book book = inventoryRepository.findById(id).get();
            book.setRentedQuantity(book.getRentedQuantity() - amount);
            book.setFreeQuantity(book.getFreeQuantity() + amount);


            inventoryRepository.save(book);
        }
        else throw new IllegalArgumentException("Book not found");
    }

    public void BookedBooks(String id, int amount) {
        if(inventoryRepository.existsById(id)){
            Book book = inventoryRepository.findById(id).get();
            book.setFreeQuantity(book.getFreeQuantity() - amount);
            book.setBookedQuantity(book.getBookedQuantity() + amount);
        }
        else throw new IllegalArgumentException("Book not found");
    }

    public void ReturnBookedBooks(String id, int amount) {
        if(inventoryRepository.existsById(id)){
            Book book = inventoryRepository.findById(id).get();
            book.setFreeQuantity(book.getFreeQuantity() - amount);
            book.setBookedQuantity(book.getBookedQuantity() + amount);
        }
        else throw new IllegalArgumentException("Book not found");
    }


    public List<Book> getAllBooks() {
        return inventoryRepository.findAll();
    }


    public boolean CheckExistCategory(String category) {

        return categoryRepository.findByName(category);
    }


    public void AddToFreeBook(String id, int amount) {
        if(inventoryRepository.existsById(id)){
            Book book = inventoryRepository.findById(id).get();
            book.setFreeQuantity(book.getFreeQuantity() + amount);
        }
    }









}
