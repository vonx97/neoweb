package com.neosoft.neoweb.repository;

import com.neosoft.neoweb.entity.Payment;
import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.entity.SubscriptionPlan;
import enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository {

    List<Payment> findBySubscription(Subscription subscription);

    List<Payment> findByStatus(PaymentStatus status);

}
