package ru.javabegin.micro.booksseller.rentapi.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.rentapi.Domain.Rent;
import ru.javabegin.micro.booksseller.rentapi.Repository.RentRepository;

@Service
public class RentService {

    private final RentRepository rentRepository;
    private final KafkaTemplate<String, RentCreatedEvent> kafkaTemplate;

    public RentService(RentRepository rentRepository) {
        this.rentRepository = rentRepository;
    }

    @Value("${rent.created.event.topic.name")


    public void createRent(Rent rent) {

        if(rent.getUserId() == null && rent.getBookId() == null && rent.getQuantity() == null) {
            throw new IllegalArgumentException("Must specify either userId or bookId");
        }

        Rent savedRent = rentRepository.save(rent);


    }
}
