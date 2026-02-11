package ru.javabegin.micro.booksseller.rentapi.Services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javabegin.micro.booksseller.rentapi.Domain.Rent;
import ru.javabegin.micro.booksseller.rentapi.Repository.RentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RentService {

    private static final Logger log = LoggerFactory.getLogger(RentService.class);
    private final RentRepository rentRepository;
    private final KafkaTemplate<String, RentCreatedEvent> kafkaTemplate;

    public RentService(RentRepository rentRepository, KafkaTemplate<String, RentCreatedEvent> kafkaTemplate) {
        this.rentRepository = rentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${category.created.event.topic.name}")
    private String categoryCreatedEventTopic;


    @Transactional
    public void createRent(Rent rent) {

        if(rent.getUserId() == null && rent.getBookId() == null && rent.getQuantity() == null) {
            throw new IllegalArgumentException("Must specify either userId or bookId");
        }

        Rent savedRent = rentRepository.save(rent);

        RentCreatedEvent event = RentCreatedEvent.newBuilder()
                .setId(Long.parseLong(savedRent.getId()))
                .setUserid(savedRent.getUserId())
                .setBookid(savedRent.getBookId())
                .setQuantity(savedRent.getQuantity())


                .build();

        try {
            kafkaTemplate.send(categoryCreatedEventTopic, event)
                    .whenComplete((result, ex) -> {
                        if(ex == null) {
                            log.info("Rent created successfully. ID: {}", savedRent.getId());
                        }
                        else {
                            log.error("Failed to send rent created event. ID: {}", savedRent.getId());
                            throw new IllegalArgumentException("Rent creation failed. ID: " + savedRent.getId());
                        }
                    });
            } catch (Exception e) {
            log.error("Rent creation failed. ID: {}", savedRent.getId());

        }

        rentRepository.save(savedRent);


    }

    @Transactional
    public void updateRent(Rent rent) {

    }


    @Transactional
    public void deleteRent(String id) {
        rentRepository.deleteById(id);
    }


}
