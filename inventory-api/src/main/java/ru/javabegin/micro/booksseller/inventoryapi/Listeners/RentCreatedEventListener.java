package ru.javabegin.micro.booksseller.inventoryapi.Listeners;

import com.smart.library.eventschemas.avro.RentCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.javabegin.micro.booksseller.inventoryapi.Services.InventoryService;

@Component
@KafkaListener(topics = "${rent.created.event.topic.name}")
public class RentCreatedEventListener {

    private final InventoryService inventoryService;

    public RentCreatedEventListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaHandler
    public void handlerRentCreated(RentCreatedEvent event) {
        inventoryService.DecreaseStock(event.getBookid(), event.getQuantity);
    }


}
