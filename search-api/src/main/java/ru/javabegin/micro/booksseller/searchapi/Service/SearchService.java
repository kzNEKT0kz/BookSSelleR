package ru.javabegin.micro.booksseller.searchapi.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.searchapi.Domain.BookDocument;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchOperations operations;


    @Cacheable(value = "book-search", key = "#query")
    public List<BookDocument> search(String query) {

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query(query)
                                .fields("title^3", "author^2", "genre", "language", "publisher")
                        )
                )
                .build();

        SearchHits<BookDocument> searchHits = operations.search(searchQuery, BookDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();


    }

    public List<BookDocument> searchByPriceRange(Integer minPrice, Integer maxPrice) {

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .range(r -> r
                                .number(n -> n
                                        .field("price")
                                        .gte((double) minPrice)
                                        .lte((double) maxPrice)
                                )
                        )


                )
                .build();

        SearchHits<BookDocument> searchHits = operations.search(searchQuery, BookDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();


    }

    @Cacheable(
            value = "book-search",
            key =
                    "T(java.util.Objects).toString(#query,'')"
                            + " + '-' + "
                            + "T(java.util.Objects).toString(#genre,'')"
                            + " + '-' + "
                            + "T(java.util.Objects).toString(#language,'')"
                            + " + '-' + "
                            + "T(java.util.Objects).toString(#minPrice,'')"
                            + " + '-' + "
                            + "T(java.util.Objects).toString(#maxPrice,'')"
                            + " + '-' + "
                            + "#page"
                            + " + '-' + "
                            + "#size"
    )

    public List<BookDocument> advancedSearch(


            String query,
            String genre,
            String language,
            Integer minPrice,
            Integer maxPrice,
            Integer page,
            Integer size
    ) {

        System.out.println("ELASTICSEARCH HIT");

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> {

                            if (query != null && !query.isBlank()) {
                                b.must(m -> m
                                        .multiMatch(mm -> mm
                                                .query(query)
                                                .fields(
                                                        "title^3",
                                                        "author^2",
                                                        "genre"
                                                )
                                        )
                                );
                            }

                            if (language != null) {
                                b.filter(f -> f
                                        .term(t -> t
                                                .field("language")
                                                .value(language)
                                        )
                                );
                            }

                            if (genre != null) {
                                b.filter(f -> f
                                        .term(t -> t
                                                .field("genre")
                                                .value(genre)
                                        )
                                );
                            }

                            if (minPrice != null || maxPrice != null) {
                                b.filter(f -> f
                                        .range(r -> r
                                                .number(n -> {

                                                    n.field("price");

                                                    if (minPrice != null) {
                                                        n.gte((double) minPrice);
                                                    }

                                                    if (maxPrice != null) {
                                                        n.lte((double) maxPrice);
                                                    }

                                                    return n;
                                                })
                                        )
                                );
                            }

                            return b;
                        })
                )
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<BookDocument> searchHits =
                operations.search(searchQuery, BookDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
