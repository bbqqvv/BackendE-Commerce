package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.request.DiscountRequest;
import org.bbqqvv.backendecommerce.dto.response.DiscountResponse;
import org.bbqqvv.backendecommerce.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(source = "applicableUsers", target = "applicableUsers", ignore = true) // Chuyển sang service xử lý
    @Mapping(source = "applicableProducts", target = "applicableProducts", ignore = true) // Chuyển sang service xử lý
    Discount toDiscount(DiscountRequest discountRequest);

    @Mapping(source = "applicableUsers", target = "applicableUsers", qualifiedByName = "mapApplicableUserIds")
    @Mapping(source = "applicableProducts", target = "applicableProducts", qualifiedByName = "mapApplicableProductIds")
    DiscountResponse toDiscountResponse(Discount discount);

    // Chuyển danh sách DiscountUser -> danh sách ID người dùng
    @Named("mapApplicableUserIds")
    default List<Long> mapApplicableUserIds(List<DiscountUser> users) {
        return users == null ? List.of() : users.stream()
                .map(discountUser -> discountUser.getUser().getId())
                .collect(Collectors.toList());
    }

    // Chuyển danh sách DiscountProduct -> danh sách ID sản phẩm
    @Named("mapApplicableProductIds")
    default List<Long> mapApplicableProductIds(List<DiscountProduct> products) {
        return products == null ? List.of() : products.stream()
                .map(discountProduct -> discountProduct.getProduct().getId())
                .collect(Collectors.toList());
    }
}
