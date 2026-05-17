package ru.javabegin.micro.booksseller.rentapi.Services;


import com.smart.library.eventschemas.avro.RentRecord;
import com.smart.library.eventschemas.avro.RentReturnRecord;

import com.smart.library.eventschemas.avro.BookedRecord;
import com.smart.library.eventschemas.avro.BookedReturnRecord;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javabegin.micro.booksseller.rentapi.Domain.Rent;
import ru.javabegin.micro.booksseller.rentapi.Repository.RentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.javabegin.micro.booksseller.rentapi.Enums.RentalOrderStatus.*;


@Service
public class RentService {

    private static final Logger log = LoggerFactory.getLogger(RentService.class);
    private final RentRepository rentRepository;
    private final KafkaTemplate<String, RentRecord> kafkaRentTemplate;
    private final KafkaTemplate<String, RentReturnRecord> kafkaRentReturnTemplate;
    private final KafkaTemplate<String, BookedRecord> kafkaBookedTemplate;
    private final KafkaTemplate<String, BookedReturnRecord> kafkaBookedReturnTemplate;

    public RentService(RentRepository rentRepository, KafkaTemplate<String, RentRecord> kafkaRentTemplate, KafkaTemplate<String, RentReturnRecord> kafkaRentReturnTemplate, KafkaTemplate<String, BookedRecord> kafkaBookedTemplate, KafkaTemplate<String, BookedReturnRecord> kafkaBookedReturnTemplate) {
        this.rentRepository = rentRepository;
        this.kafkaRentTemplate = kafkaRentTemplate;
        this.kafkaRentReturnTemplate = kafkaRentReturnTemplate;
        this.kafkaBookedTemplate = kafkaBookedTemplate;
        this.kafkaBookedReturnTemplate = kafkaBookedReturnTemplate;
    }

    @Value("${rent.record.topic.name}")
    private String rentCreatedEventTopic;

    @Value("${rent.return.record.topic.name}")
    private String rentReturnEventTopic;

    @Value("${booked.record.topic.name}")
    private String bookedRecordTopic;

    @Value("${booked.return.record.topic.name}")
    private String bookedReturnRecordTopic;


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
        savedRent.setStatus(RENTED);
        rentRepository.save(savedRent);
    }


    @Transactional
    public void returnRent(Rent rent) {


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
                            log.info("Rent returned successfully. ID: {}", rent.getId());
                        }
                        else {
                            log.error("Failed to send rent returned event. ID: {}", rent.getId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Rent return failed. ID: {}", rent.getId());
        }
        savedRent.setStatus(RETURNED);
        rentRepository.save(rent);

    }

    @Transactional
    public void updateRent(Rent rent) {

    }

    @Transactional

    //Сюда должен приходить не Rent а BookedDTO с RecordID, BookID, Quantity

    public void createBookedBook(Rent rent) {
        if(rent.getUserId() == null && rent.getBookId() == null && rent.getQuantity() == null) {
            throw new IllegalArgumentException("Must specify either userId or bookId");
        }

        Rent savedRent = rentRepository.save(rent);

        BookedRecord event = BookedRecord.newBuilder()
                .setRecordId(Long.parseLong(savedRent.getId()))
                .setBookID(savedRent.getBookId())
                .setQuantity(savedRent.getQuantity())
                .build();

        try{
            kafkaBookedTemplate.send(bookedRecordTopic, event)
                    .whenComplete((result, ex) -> {
                        if(ex == null) {
                            log.info("Booked successfully. ID: {}", savedRent.getId());
                        }
                        else {
                            log.error("Failed to send booked event. ID: {}", savedRent.getId(), ex);
                        }
                    });


        }
        catch(Exception e) {
            log.error("Booked creation failed. ID: {}", savedRent.getId());
        }

        savedRent.setStatus(BOOKED);
    }

    @Transactional
    public void CancelBookedBook(Rent rent) {
        if(rent.getUserId() == null && rent.getBookId() == null && rent.getQuantity() == null) {
            throw new IllegalArgumentException("Must specify either userId or bookId");
        }
        Rent savedRent = rentRepository.save(rent);

        BookedReturnRecord event = BookedReturnRecord.newBuilder()
                .setRecordId(Long.parseLong(savedRent.getId()))
                .setBookID(savedRent.getBookId())
                .setQuantity(savedRent.getQuantity())
                .build();

        try {
            kafkaBookedReturnTemplate.send(bookedReturnRecordTopic, event)
                    .whenComplete((result, ex) -> {
                        if(ex == null) {
                            log.info("Booked successfully. ID: {}", savedRent.getId());
                        }
                        else {
                            log.error("Failed to send booked event. ID: {}", savedRent.getId(), ex);
                        }
                    });
        }
        catch(Exception e) {

            log.error("Booked creation failed. ID: {}", savedRent.getId());
        }

        savedRent.setStatus(CANCELLED);

    }








}
