package ru.javabegin.micro.booksseller.inventoryapi.Entities;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "books")
public class Book {
    @Id
    private String id;

    private Boolean isAvailable;

    private String skuCode;

    private String title;

    @NotNull
    private Integer stockQuantity;

    private Integer rentedQuantity;

    private String description;

    private String subtitle;

    private String author;

    private String publisher;

    private LocalDate publicationDate;

    private String isbn13;

    private String language;

    @NotNull
    private Integer pageCount;

    private String genre;

    private String edition;

    private String  series;

    // getters & setters
}