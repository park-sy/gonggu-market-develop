package com.gonggu.pay.repository;

import com.gonggu.pay.domain.Payment;
import com.gonggu.pay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByUser(User user);
}
