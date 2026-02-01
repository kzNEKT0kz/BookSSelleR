package ru.javabegin.micro.booksseller.inventoryapi.Listeners;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.javabegin.micro.booksseller.rentapi.Services.RentService;

@Component
@KafkaListener(topics = "${rent.created.event.topic.name}")
public class RentCreatedEventListener {


}
