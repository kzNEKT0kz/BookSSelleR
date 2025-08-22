package ru.javabegin.micro.booksseller.catalogapi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javabegin.micro.booksseller.catalogapi.Entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
