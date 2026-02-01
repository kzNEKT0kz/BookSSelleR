package ru.javabegin.micro.booksseller.rentapi.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.javabegin.micro.booksseller.rentapi.Domain.Rent;

public interface RentRepository extends MongoRepository<Rent, String> {
}
