package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.request.CategoryRequest;
import org.bbqqvv.backendecommerce.dto.response.CategoryResponse;
import org.bbqqvv.backendecommerce.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SizeMapper.class})
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    Category categoryRequestToCategory(CategoryRequest categoryRequest);
    @Mapping(target = "sizes", source = "sizeCategories")
    CategoryResponse categoryToCategoryResponse(Category category);
}