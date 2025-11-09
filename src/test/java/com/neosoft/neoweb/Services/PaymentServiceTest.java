package com.neosoft.neoweb.Services;


import com.neosoft.neoweb.entity.Payment;
import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.entity.SubscriptionPlan;
import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.repository.PaymentRepository;
import com.neosoft.neoweb.repository.SubscriptionRepository;

import com.neosoft.neoweb.services.PaymentService;
import enums.CurrencyType;
import enums.PaymentMethods;
import enums.PaymentStatus;
import enums.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private PaymentService paymentService; // real instance

    private Subscription subscription;
    private LocalDateTime now;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        now = LocalDateTime.now();
        User user = new User("testuser", "secret", "Name", "Surname", now);
        SubscriptionPlan plan = new SubscriptionPlan("Basic","",new BigDecimal("33"),31,5);
        subscription = new Subscription(user, plan, now, now.plusDays(6), SubscriptionStatus.ACTIVE,false);
    }

    @Test
    void shouldCreatePayment_whenSubscriptionExists() {
        BigDecimal amount = BigDecimal.valueOf(22);
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.createPayment(1, amount, CurrencyType.TRY, now, PaymentMethods.CASH, PaymentStatus.SUCCESS);

        assertThat(result.getSubscription()).isEqualTo(subscription);
        assertThat(result.getAmount()).isEqualByComparingTo(amount);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void shouldThrowException_whenSubscriptionNotFound() {
        when(subscriptionRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.createPayment(1, BigDecimal.valueOf(100), CurrencyType.TRY, now, PaymentMethods.CASH, PaymentStatus.SUCCESS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Subscription not found");
    }

    @Test
    void shouldFindAllPaymentsForSubscription() {
        Payment p1 = new Payment(subscription, BigDecimal.valueOf(100), CurrencyType.TRY, now, PaymentMethods.CASH, PaymentStatus.SUCCESS);
        Payment p2 = new Payment(subscription, BigDecimal.valueOf(100), CurrencyType.TRY, now, PaymentMethods.CASH, PaymentStatus.SUCCESS);
        when(paymentRepository.findBySubscription(subscription)).thenReturn(List.of(p1, p2));

        List<Payment> result = paymentService.getPaymentsBySubscription(subscription);

        assertThat(result).hasSize(2);
        verify(paymentRepository, times(1)).findBySubscription(subscription);
    }

    @Test
    void shouldFindLatestPaymentForSubscription() {
        Payment latest = new Payment(subscription, BigDecimal.valueOf(100), CurrencyType.TRY, now, PaymentMethods.CASH, PaymentStatus.SUCCESS);
        when(paymentRepository.findTopBySubscriptionOrderByPaymentDateDesc(subscription))
                .thenReturn(Optional.of(latest));

        Optional<Payment> result = paymentService.getLatestPayment(subscription);

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }
}


