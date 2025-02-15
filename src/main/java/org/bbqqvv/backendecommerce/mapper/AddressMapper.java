package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.request.AddressRequest;
import org.bbqqvv.backendecommerce.dto.response.AddressResponse;
import org.bbqqvv.backendecommerce.entity.Address;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(target = "defaultAddress", source = "defaultAddress") // ✅ Map từ defaultAddress -> isDefault
    Address toAddress(AddressRequest addressRequest);
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "defaultAddress", source = "defaultAddress") // ✅ Map từ isDefault -> defaultAddress
    AddressResponse toAddressResponse(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(AddressRequest addressRequest, @MappingTarget Address existingAddress);
}
