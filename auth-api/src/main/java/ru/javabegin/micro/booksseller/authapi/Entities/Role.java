package ru.javabegin.micro.booksseller.authapi.Entities;

import jakarta.persistence.*;

@Entity
@Table (name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "entity_id", nullable = false)
    private Long entity_Id;
    @Column(nullable = false)
    private String role;


    public Role() {
    }

    public Role(Long id, Long entity_Id, String role) {
        this.id = id;
        this.entity_Id = entity_Id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntity_Id() {
        return entity_Id;
    }

    public void setEntity_Id(Long entity_Id) {
        this.entity_Id = entity_Id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", entity_Id=" + entity_Id +
                ", role='" + role + '\'' +
                '}';
    }
}
