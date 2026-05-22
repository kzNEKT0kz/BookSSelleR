package ru.javabegin.micro.booksseller.searchapi.Controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javabegin.micro.booksseller.searchapi.Domain.BookDocument;
import ru.javabegin.micro.booksseller.searchapi.Service.SearchService;

import java.util.List;

@RestController
@RequestMapping(path = "/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public List<BookDocument> search(@RequestParam String query) {
        return searchService.search(query);
    }

    @GetMapping("/price")
    public List<BookDocument> search(@RequestParam Integer minPrice, @RequestParam Integer maxPrice) {
        return  searchService.searchByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/advanced")
    public List<BookDocument> advancedSearch(

            @RequestParam(required = false)
            String query,

            @RequestParam(required = false)
            String genre,

            @RequestParam(required = false)
            String language,

            @RequestParam(required = false)
            Integer minPrice,

            @RequestParam(required = false)
            Integer maxPrice
    ) {

        return searchService.advancedSearch(
                query,
                genre,
                language,
                minPrice,
                maxPrice
        );
    }


}
