package ru.javabegin.micro.booksseller.authapi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.javabegin.micro.booksseller.authapi.Entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query(value = "select * from roles where  entity_id = :entity_id", nativeQuery = true)
    Optional<Role> findEntityById(Long entity_id);
}
