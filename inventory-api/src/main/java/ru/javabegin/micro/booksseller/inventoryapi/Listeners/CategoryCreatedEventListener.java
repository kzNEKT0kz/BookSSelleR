package ru.javabegin.micro.booksseller.inventoryapi.Listeners;

import com.smart.library.eventschemas.avro.CategoryCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.javabegin.micro.booksseller.inventoryapi.Domain.Category;
import ru.javabegin.micro.booksseller.inventoryapi.Services.CategoryService;

@Component
@KafkaListener(topics = "${category.created.event.topic.name}")
public class CategoryCreatedEventListener {

    private final CategoryService categoryService;
    public CategoryCreatedEventListener(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @KafkaHandler
   public void handlerCategoryCreated(CategoryCreatedEvent event) {
        Category category = new Category(String.valueOf(event.getId()),event.getCategoryName());
        categoryService.create(category);
    }


}
