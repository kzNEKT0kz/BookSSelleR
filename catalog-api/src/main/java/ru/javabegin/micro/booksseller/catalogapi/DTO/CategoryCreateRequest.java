package ru.javabegin.micro.booksseller.catalogapi.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryCreateRequest {

    @NotBlank
    private String name;

    private Long parent_id;

}
