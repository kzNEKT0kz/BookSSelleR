package ru.javabegin.micro.booksseller.catalogapi.Mappers;


import org.mapstruct.Mapper;
import ru.javabegin.micro.booksseller.catalogapi.DTO.CategoryCreateRequest;
import ru.javabegin.micro.booksseller.catalogapi.Entities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryCreateRequest toDto(Category category);

    Category toEntity(CategoryCreateRequest categoryDTO);
}
