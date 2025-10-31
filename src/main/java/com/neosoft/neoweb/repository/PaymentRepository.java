package com.neosoft.neoweb.repository;

import com.neosoft.neoweb.entity.Payment;
import com.neosoft.neoweb.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {

    Optional<Payment> findTopBySubscriptionOrderByPaymentDateDesc(Subscription subscription);
    List<Payment> findBySubscription(Subscription subscription);

}
