package com.neosoft.neoweb.services;

import com.neosoft.neoweb.entity.Payment;
import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.repository.PaymentRepository;
import com.neosoft.neoweb.repository.SubscriptionRepository;
import enums.CurrencyType;
import enums.PaymentMethods;
import enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PaymentService(PaymentRepository paymentRepository, SubscriptionRepository subscriptionRepository) {
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public Payment createPayment(Integer subscriptionId, BigDecimal amount, CurrencyType currencyType, LocalDateTime paymentDate, PaymentMethods paymentMethod, PaymentStatus paymentStatus) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        Payment payment = new Payment(subscription,amount,currencyType,paymentDate,paymentMethod,paymentStatus);

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsBySubscription(Subscription subscription) {
        return paymentRepository.findBySubscription(subscription);
    }

    public Optional<Payment> getLatestPayment(Subscription subscription) {
        return paymentRepository.findTopBySubscriptionOrderByPaymentDateDesc(subscription);
    }
}
