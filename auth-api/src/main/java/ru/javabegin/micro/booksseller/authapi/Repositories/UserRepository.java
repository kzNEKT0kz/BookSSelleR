package ru.javabegin.micro.booksseller.authapi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.javabegin.micro.booksseller.authapi.Entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users where  email = :email", nativeQuery = true)
    Optional<User> findByEmail(String email);
}
