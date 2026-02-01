package ru.javabegin.micro.booksseller.catalogapi.Services;


import com.smart.library.eventschemas.avro.CategoryCreatedEvent;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.catalogapi.Entities.Category;
import ru.javabegin.micro.booksseller.catalogapi.Repositories.CategoryRepository;


@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, CategoryCreatedEvent> kafkaTemplate;

    public CategoryService(CategoryRepository categoryRepository,
                           KafkaTemplate<String, CategoryCreatedEvent> kafkaTemplate) {
        this.categoryRepository = categoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${category.created.event.topic.name}")
    private String categoryCreatedEventTopicName;

    @Transactional
    public Category create(Category category) {


        if(categoryRepository.findByName(category.getName()).isPresent()) {
            throw new IllegalStateException("Category with name " + category.getName() + " already exists");
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        Category savedCategory = categoryRepository.save(category);

        CategoryCreatedEvent event = CategoryCreatedEvent.newBuilder()
                .setId(savedCategory.getId())
                .setCategoryName(savedCategory.getName())
                .build();

        try {
            kafkaTemplate.send(categoryCreatedEventTopicName, String.valueOf(savedCategory.getId()), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Category created event sent successfully. ID: {}", savedCategory.getId());
                        } else {
                            log.error("Failed to send category created event. ID: {}", savedCategory.getId(), ex);
                            throw new IllegalArgumentException("Failed to send category created event. ID: " + savedCategory.getId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending Kafka message for category ID: {}", savedCategory.getId(), e);
        }

        return savedCategory;

    }

    @Transactional
    public void delete(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return;
        }
        throw new IllegalStateException("Category already does not exist");
    }

    @Transactional
    public Category update(Category category) {
        if(categoryRepository.existsById(category.getId())){
            Category currentCategory = new Category();
            currentCategory.setName(category.getName());
            currentCategory.setParent(category.getParent());
            currentCategory.setSubcategory(category.getSubcategory());
            return categoryRepository.save(currentCategory);
        }
        throw new IllegalStateException("Category does not exist");
    }

}
