package ru.javabegin.micro.booksseller.inventoryapi.Listeners;

import com.smart.library.eventschemas.avro.RentRecord;
import com.smart.library.eventschemas.avro.RentReturnRecord;
import com.smart.library.eventschemas.avro.BookedRecord;
import com.smart.library.eventschemas.avro.BookedReturnRecord;


import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.javabegin.micro.booksseller.inventoryapi.Services.InventoryService;

@Component
@KafkaListener(topics = "${rent.record.topic.name}")
@KafkaListener(topics = "${rent.return.record.topic.name}")

public class RentCreatedEventListener {

    private final InventoryService inventoryService;
    public RentCreatedEventListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaHandler
    public void handlerRentCreated(RentRecord rentRecord) {
        inventoryService.RentBooks(rentRecord.getBookID(), rentRecord.getQuantity());
    }

    @KafkaHandler
    public void handlerRentReturn(RentReturnRecord rentReturnRecord) {
        inventoryService.ReturnRentBooks(rentReturnRecord.getBookID(), rentReturnRecord.getQuantity());
    }

    @KafkaHandler
    public void handlerBookedBook(BookedRecord bookedRecord) {
        inventoryService.BookedBooks(bookedRecord.getBookID(), bookedRecord.getQuantity());
    }

    @KafkaHandler
    public void handlerBookedReturn(BookedReturnRecord bookedReturnRecord) {
        inventoryService.ReturnBookedBooks(bookedReturnRecord.getBookID(), bookedReturnRecord.getQuantity());
    }






}
