package ru.javabegin.micro.booksseller.rentapi.Services;


import com.smart.library.eventschemas.avro.RentReturnRecord;
import com.smart.library.eventschemas.avro.RentRecord;
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
    private final KafkaTemplate<String, RentRecord> kafkaRentTemplate;
    private final KafkaTemplate<String, RentReturnRecord> kafkaRentReturnTemplate;

    public RentService(RentRepository rentRepository, KafkaTemplate<String, RentRecord> kafkaRentTemplate, KafkaTemplate<String, RentReturnRecord> kafkaRentReturnTemplate) {
        this.rentRepository = rentRepository;
        this.kafkaRentTemplate = kafkaRentTemplate;
        this.kafkaRentReturnTemplate = kafkaRentReturnTemplate;
    }

    @Value("${rent.created.event.topic.name}")
    private String rentCreatedEventTopic;

    @Value("${rent.status.event.topic.name}")
    private String rentReturnEventTopic;


    @Transactional
    public void createRent(Rent rent) {

        if(rent.getUserId() == null && rent.getBookId() == null && rent.getQuantity() == null) {
            throw new IllegalArgumentException("Must specify either userId or bookId");
        }

        Rent savedRent = rentRepository.save(rent);



        RentRecord event = RentRecord.newBuilder()
                .setRecordId(Long.parseLong(savedRent.getId()))
                .setBookID(savedRent.getBookId())
                .setQuantity(savedRent.getQuantity())

                .build();

        try {
            kafkaRentTemplate.send(rentCreatedEventTopic, event)
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
    public void cancelRent(Rent rent) {


        //Сюда должен приходить не Rent а DTO с RecordID, BookID, Quantity



        if(rent.getUserId() == null && rent.getBookId() == null && rent.getQuantity() == null) {
            throw new IllegalArgumentException("Must specify either userId or bookId");
        }

        Rent savedRent = rentRepository.save(rent);

        RentReturnRecord event = RentReturnRecord.newBuilder()
                .setRecordId(Long.parseLong(savedRent.getId()))
                .setBookID(savedRent.getBookId())
                .setQuantity(savedRent.getQuantity())
                .build();

        try {
            kafkaRentReturnTemplate.send(rentReturnEventTopic, event)
                    .whenComplete((result, ex) -> {
                        if(ex == null) {
                            log.info("Rent cancelled successfully. ID: {}", rent.getId());
                        }
                        else {
                            log.error("Failed to send rent cancelled event. ID: {}", rent.getId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Rent cancel failed. ID: {}", rent.getId());
        }
        rentRepository.save(rent);

    }

    @Transactional
    public void updateRent(Rent rent) {

    }




}
