package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.request.AddressRequest;
import org.bbqqvv.backendecommerce.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {

    AddressResponse createAddress(AddressRequest addressRequest);

    AddressResponse updateAddress(Long addressId, AddressRequest addressRequest);

    List<AddressResponse> getAddressesByUser();

    AddressResponse getAddressById(Long addressId);

    void deleteAddress(Long addressId);

    AddressResponse setDefaultAddress(Long addressId);
}
