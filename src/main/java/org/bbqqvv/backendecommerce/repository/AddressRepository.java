package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    boolean existsAddressById(Long id);
    List<Address> findAllByUserId(Long userId);
    Optional<Address> findDefaultAddressByUserId(Long userId);
    List<Address> findDefaultAddressesByUserId(Long userId);
}
