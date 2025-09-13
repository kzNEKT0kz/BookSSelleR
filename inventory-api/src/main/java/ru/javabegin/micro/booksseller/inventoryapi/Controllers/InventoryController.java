package ru.javabegin.micro.booksseller.inventoryapi.Controllers;

import org.springframework.web.bind.annotation.*;
import ru.javabegin.micro.booksseller.inventoryapi.Collections.Book;
import ru.javabegin.micro.booksseller.inventoryapi.Services.InventoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping(path = "/addBook")
    public Book addBook(@RequestBody Book book) {
        return inventoryService.addBook(book);
    }

    @DeleteMapping(path = "/removeBook/{id}")
    public void removeBook(@PathVariable String id) {
        inventoryService.removeBook(id);
    }

    @PutMapping(path = "/updateBook")
    public Book updateBook(@RequestBody Book book) {
        return inventoryService.updateBook(book);
    }

    @GetMapping(path = "/getAll")
    public List<Book> getAll() {
        return inventoryService.getAllBooks(); // example from service layer
    }

}
