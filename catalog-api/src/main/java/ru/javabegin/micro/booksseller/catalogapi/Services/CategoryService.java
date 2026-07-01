package ru.javabegin.micro.booksseller.catalogapi.Services;


import com.smart.library.eventschemas.avro.CategoryCreatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.javabegin.micro.booksseller.catalogapi.DTO.CategoryCreateRequest;
import ru.javabegin.micro.booksseller.catalogapi.Entities.Category;
import ru.javabegin.micro.booksseller.catalogapi.Mappers.CategoryMapper;
import ru.javabegin.micro.booksseller.catalogapi.Repositories.CategoryRepository;


@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, CategoryCreatedEvent> kafkaTemplate;

    private final CategoryMapper categoryMapper;

    public CategoryCreateRequest getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        return categoryMapper.toDto(category);
    }

    @Value("${category.created.event.topic.name}")
    private String categoryCreatedEventTopicName;

    @Transactional
    public void create(CategoryCreateRequest categoryCreateRequest) {

        if(categoryRepository.findByName(categoryCreateRequest.getName()).isPresent()) {
            throw new IllegalStateException("Category with name " + categoryCreateRequest.getName() + " already exists");
        }

        Category category = categoryMapper.toEntity(categoryCreateRequest);

        if (categoryCreateRequest.getParent_id() != null) {
            Category parent = categoryRepository.findById(categoryCreateRequest.getParent_id()).orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);

        CategoryCreatedEvent event = CategoryCreatedEvent.newBuilder()
                .setId(savedCategory.getId())
                .setCategoryName(savedCategory.getName())
                .build();

        try {
            kafkaTemplate.send(categoryCreatedEventTopicName, String.valueOf(savedCategory.getId()), event).get();

        } catch (Exception e) {
            log.error("Error sending Kafka message for category ID: {}", savedCategory.getId(), e);

            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalStateException("Category already does not exist");
        }
        categoryRepository.deleteById(id);

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
