package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.SupportItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportItemRepository extends JpaRepository<SupportItem, Long> {

}
