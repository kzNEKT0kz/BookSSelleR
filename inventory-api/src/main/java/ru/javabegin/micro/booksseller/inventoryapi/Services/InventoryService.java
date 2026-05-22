package ru.javabegin.micro.booksseller.inventoryapi.Services;

import com.smart.library.eventschemas.avro.DeleteBookEvent;
import com.smart.library.eventschemas.avro.InventoryCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.inventoryapi.Domain.Book;
import ru.javabegin.micro.booksseller.inventoryapi.Repository.CategoryRepository;
import ru.javabegin.micro.booksseller.inventoryapi.Repository.InventoryRepository;

import java.util.List;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, InventoryCreatedEvent> bookCreatedkafkaTemplate;
    private final KafkaTemplate<String, DeleteBookEvent> bookDeleteKafkaTemplate;

    public InventoryService(InventoryRepository inventoryRepository , CategoryRepository categoryRepository, KafkaTemplate<String, InventoryCreatedEvent> bookCreatedkafkaTemplate, KafkaTemplate<String, DeleteBookEvent> bookDeleteKafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.categoryRepository = categoryRepository;
        this.bookCreatedkafkaTemplate = bookCreatedkafkaTemplate;
        this.bookDeleteKafkaTemplate = bookDeleteKafkaTemplate;

    }


    @Value("${inventory.record.topic.name}")
    private String inventoryRecordTopic;

    @Value("${inventory.record.topic.name}")
    private String deleteBookRecordTopic;

    public void addBook(Book book) {

        if(!CheckExistCategory(book.getGenre())){
             throw new  IllegalArgumentException("Genre does not exist");
        }


        InventoryCreatedEvent event = InventoryCreatedEvent.newBuilder()
                .setBookID(book.getId())
                .setBookName(book.getTitle())
                .setAuthor(book.getAuthor())
                .setGenre(book.getGenre())
                .setLanguage(book.getLanguage())
                .setPublisher(book.getPublisher())
                .setPrice(book.getPrice())
                .build();

        try{
            bookCreatedkafkaTemplate.send(inventoryRecordTopic, event)
                    .whenComplete((result, ex) -> {
                        if(ex == null) {
                            log.info("Book created successfully. ID: {}", book.getId());
                        }
                        else {
                            log.error("Failed to send book created event. ID: {}", book.getId());
                            throw new IllegalArgumentException("Book creation failed. ID: " + book.getId());
                        }
                    });

        }catch (Exception e){
            log.error("Failed to send bookk created event. ID: {}", book.getId());
        }

         inventoryRepository.save(book);
    }

    public void removeBook(String id) {

        DeleteBookEvent event = DeleteBookEvent.newBuilder()
                .setBookID(id)
                .build();

        try{
            bookDeleteKafkaTemplate.send(deleteBookRecordTopic, event)
                    .whenComplete((result, ex) -> {
                        if(ex == null) {
                            log.info("Book deleted successfully. ID: {}", id);
                        }
                        else {
                            log.error("Failed to delete book event. ID: {}", id);
                        }
                    });
        }catch (Exception e){
            log.error("Failed to delete book event. ID: {}", id);
        }


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

    public void AddToFreeBook(String id, int amount) {
        if(inventoryRepository.existsById(id)){
            Book book = inventoryRepository.findById(id).get();
            book.setFreeQuantity(book.getFreeQuantity() + amount);
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












}
