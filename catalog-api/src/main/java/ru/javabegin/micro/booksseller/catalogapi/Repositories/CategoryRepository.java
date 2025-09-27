package ru.javabegin.micro.booksseller.catalogapi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.javabegin.micro.booksseller.catalogapi.Entities.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    @Query(value = "select * from catalog where name = :name", nativeQuery = true)
    Optional<Category> findByName(String name);
}
