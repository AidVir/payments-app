package com.aidas.payments.repository;

import com.aidas.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCancelledFalse();

    @Query("SELECT p FROM Payment p WHERE p.cancelled = false "
            + "AND (:min IS NULL OR p.amount >= :min) "
            + "AND (:max IS NULL OR p.amount <= :max)")
    List<Payment> findByAmountRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
