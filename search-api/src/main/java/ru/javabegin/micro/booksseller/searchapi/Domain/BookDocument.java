package ru.javabegin.micro.booksseller.searchapi.Domain;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Setter
@Getter
@Document(indexName = "books")
@Data
public class BookDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String author;

    @Field(type = FieldType.Text)
    private String genre;

    @Field(type = FieldType.Keyword)
    private String language;

    @Field(type = FieldType.Keyword)
    private String publisher;

    @Field(type = FieldType.Integer)
    private Integer price;


    public BookDocument(String id, String title, String author, String genre, String language, String publisher, Integer price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.language = language;
        this.publisher = publisher;
        this.price = price;
    }

    public BookDocument() {
    }

    @Override
    public String toString() {
        return "BookDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", language='" + language + '\'' +
                ", publisher='" + publisher + '\'' +
                ", price=" + price +
                '}';
    }
}
