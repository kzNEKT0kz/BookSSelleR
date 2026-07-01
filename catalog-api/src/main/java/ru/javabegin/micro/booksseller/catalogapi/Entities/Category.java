package ru.javabegin.micro.booksseller.catalogapi.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "catalog")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> subcategory = new ArrayList<Category>();

    public Category() {

    }

    public Category(Long id, String name, Category parent, List<Category> subcategory) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.subcategory = subcategory;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", subcategory=" + subcategory +
                '}';
    }
}
