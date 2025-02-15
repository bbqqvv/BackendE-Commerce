package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserId(Long userId);
    List<Address> findDefaultAddressesByUserId(Long userId);
    Optional<Address> findByIdAndUserId(Long addressId, Long id);
}
