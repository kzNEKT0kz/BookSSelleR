package ru.javabegin.micro.booksseller.searchapi.Reposiroty;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.javabegin.micro.booksseller.searchapi.Domain.BookDocument;

public interface  SearchRepository extends ElasticsearchRepository<BookDocument, String> {

}
