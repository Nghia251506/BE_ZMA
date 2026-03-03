package com.example.demo.Mapper;

import com.example.demo.Dto.Category.CategoryRequest;
import com.example.demo.Dto.Category.CategoryResponse;
import com.example.demo.Entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest request);
    CategoryResponse toResponse(Category entity);
    List<CategoryResponse> toResponseList(List<Category> entities);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category entity);
}
