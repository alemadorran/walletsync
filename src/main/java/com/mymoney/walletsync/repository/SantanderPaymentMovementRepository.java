package com.mymoney.walletsync.repository;

import com.mymoney.walletsync.model.santander.entity.SantanderPaymentMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SantanderPaymentMovementRepository extends JpaRepository<SantanderPaymentMovement, Long> {

    @Query(value = "SELECT * FROM santander_payment_movements WHERE DATE_PART('year', operation_date) = :year",
            nativeQuery = true)
    List<SantanderPaymentMovement> findByYear(@Param("year") Long year);

}
