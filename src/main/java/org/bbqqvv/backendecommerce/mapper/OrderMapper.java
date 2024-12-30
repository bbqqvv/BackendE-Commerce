package org.bbqqvv.backendecommerce.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

}
