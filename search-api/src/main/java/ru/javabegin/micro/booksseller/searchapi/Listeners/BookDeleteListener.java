package ru.javabegin.micro.booksseller.searchapi.Listeners;


import com.smart.library.eventschemas.avro.DeleteBookEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.javabegin.micro.booksseller.searchapi.Reposiroty.SearchRepository;

@Component
@KafkaListener(topics = "${book.delete.topic.name}")
public class BookDeleteListener {

    private final SearchRepository searchRepository;
    public BookDeleteListener(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }



   @KafkaHandler
    private void handlerBookDelete(DeleteBookEvent event) {
        searchRepository.deleteById(event.getBookID());
   }

}
