package ru.javabegin.micro.booksseller.authapi.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.javabegin.micro.booksseller.authapi.Entities.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Query(value = "select * from admins where  email = :email", nativeQuery = true)
    Optional<Admin> findByEmail(String email);
}
