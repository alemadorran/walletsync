package com.mymoney.walletsync.repository;

import com.mymoney.walletsync.model.santander.entity.SantanderPaymentMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SantanderPaymentMovementRepository extends JpaRepository<SantanderPaymentMovement, Long> {
}
