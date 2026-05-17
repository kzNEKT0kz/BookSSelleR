package ru.javabegin.micro.booksseller.rentapi.Domain;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.javabegin.micro.booksseller.rentapi.Enums.RentalOrderStatus;

import java.time.LocalDate;

@Document(collection="rents")
public class Rent {

    @Id
    private String id;

    @NotNull
    private String userId;

    @NotNull
    private String bookId;

    @NotNull
    private Integer quantity;

    private RentalOrderStatus status;

    private LocalDate rentalDate;

    private LocalDate returnDate;


    public Rent( ) {

    }

    public Rent(String id, String userId, String bookId, Integer quantity, RentalOrderStatus status, LocalDate rentalDate, LocalDate returnDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.status = status;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public RentalOrderStatus getStatus() {
        return status;
    }

    public void setStatus(RentalOrderStatus status) {
        this.status = status;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Rent{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", quantity=" + quantity +
                ", status=" + status +
                ", rentalDate=" + rentalDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
