package ru.javabegin.micro.booksseller.searchapi.Listeners;


import com.smart.library.eventschemas.avro.InventoryCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.javabegin.micro.booksseller.searchapi.Domain.BookDocument;
import ru.javabegin.micro.booksseller.searchapi.Reposiroty.SearchRepository;
import ru.javabegin.micro.booksseller.searchapi.Service.SearchService;

@Component
@KafkaListener(topics = "${inventory.record.topic.name}")
public class BookCreatedListener {

   private final SearchRepository searchRepository;

   public BookCreatedListener(SearchRepository searchRepository) {
       this.searchRepository = searchRepository;
   }

    @KafkaHandler
    public void handlerBookCreated(InventoryCreatedEvent event) {

        BookDocument bookDocument = new BookDocument();
        bookDocument.setId(event.getBookID());
        bookDocument.setTitle(event.getBookName());
        bookDocument.setAuthor(event.getAuthor());
        bookDocument.setGenre(event.getGenre());
        bookDocument.setLanguage(event.getLanguage());
        bookDocument.setPublisher(event.getPublisher());
        bookDocument.setPrice(event.getPrice());
        searchRepository.save(bookDocument);
    }


}
