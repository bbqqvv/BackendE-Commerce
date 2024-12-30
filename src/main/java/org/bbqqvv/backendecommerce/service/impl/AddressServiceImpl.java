package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.dto.request.AddressRequest;
import org.bbqqvv.backendecommerce.dto.response.AddressResponse;
import org.bbqqvv.backendecommerce.entity.Address;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.AddressMapper;
import org.bbqqvv.backendecommerce.repository.AddressRepository;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.bbqqvv.backendecommerce.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressServiceImpl implements AddressService {

    AddressRepository addressRepository;
    AddressMapper addressMapper;
    UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest addressRequest) {
        User user = findUserById(addressRequest.getUser().getId());

        Address address = addressMapper.toAddress(addressRequest);
        address.setUser(user);

        if (addressRequest.isDefault()) {
            log.info("Setting address with id {} as default for user {}", address.getId(), user.getId());
            setDefaultAddressForUser(user.getId(), null);
            address.setDefault(true);
        }
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toAddressResponse(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressRequest addressRequest) {
        Address existingAddress = findAddressById(addressId);

        // Cập nhật thông tin địa chỉ
        addressMapper.updateEntityFromRequest(addressRequest, existingAddress);

        // Kiểm tra nếu là địa chỉ mặc định
        if (addressRequest.isDefault() && !existingAddress.isDefault()) {
            setDefaultAddressForUser(existingAddress.getUser().getId(), addressId);
            existingAddress.setDefault(true);
        }

        Address updatedAddress = addressRepository.save(existingAddress);
        return addressMapper.toAddressResponse(updatedAddress);
    }

    @Override
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        if (addresses.isEmpty()) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }
        return addresses.stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse getAddressById(Long addressId) {
        Address address = findAddressById(addressId);
        return addressMapper.toAddressResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        Address address = findAddressById(addressId);

        if (address.isDefault()) {
            throw new AppException(ErrorCode.ADDRESS_DEFAULT_CANNOT_DELETE);
        }
        addressRepository.deleteById(addressId);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long addressId) {
        Address address = findAddressById(addressId);

        setDefaultAddressForUser(address.getUser().getId(), addressId);
        address.setDefault(true);
        Address updatedAddress = addressRepository.save(address);

        return addressMapper.toAddressResponse(updatedAddress);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
    }

    private Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
    }

    private void setDefaultAddressForUser(Long userId, Long newDefaultAddressId) {
        List<Address> defaultAddresses = addressRepository.findDefaultAddressesByUserId(userId);

        if (!defaultAddresses.isEmpty()) {
            log.info("User with ID {} has {} default addresses", userId, defaultAddresses.size());
        }

        defaultAddresses.stream()
                .filter(addr -> !addr.getId().equals(newDefaultAddressId))
                .forEach(addr -> {
                    addr.setDefault(false);
                    addressRepository.save(addr); // Lưu cập nhật
                });

        if (newDefaultAddressId != null) {
            Address newDefaultAddress = addressRepository.findById(newDefaultAddressId)
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
            if (!newDefaultAddress.isDefault()) {
                newDefaultAddress.setDefault(true);
                addressRepository.save(newDefaultAddress); // Lưu địa chỉ mới làm mặc định
            }
        }
    }
}
