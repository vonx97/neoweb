package com.neosoft.neoweb.Services;
import com.neosoft.neoweb.dto.SubscriptionRenewDTO;
import com.neosoft.neoweb.dto.SubscriptionRequestDTO;
import com.neosoft.neoweb.dto.SubscriptionUpdateDTO;
import com.neosoft.neoweb.entity.*;
import com.neosoft.neoweb.repository.*;
import com.neosoft.neoweb.services.SubscriptionService;
import enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User user;
    private SubscriptionPlan plan;
    private LocalDateTime now;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        now = LocalDateTime.now();

        user = new User("testuser", "secret", "Name", "Surname", now);
        user.setId(1);

        plan = new SubscriptionPlan("Basic","", new BigDecimal("22.3"), 7,5);
        plan.setId(1);
    }

    // === CREATE ===
    @Test
    void shouldCreateSubscription_whenUserAndPlanExist() {
        SubscriptionRequestDTO dto = new SubscriptionRequestDTO();
        dto.setUserId(1);
        dto.setPlanId(1);
        dto.setAutoRenew(true);
        dto.setCurrency(CurrencyType.USD);
        dto.setPaymentMethod(PaymentMethods.CREDIT_CARD);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription result = subscriptionService.createSubscription(dto);

        assertThat(result.getCustomer()).isEqualTo(user);
        assertThat(result.getPlan()).isEqualTo(plan);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getSubscription()).isEqualTo(result);
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        SubscriptionRequestDTO dto = new SubscriptionRequestDTO();
        dto.setUserId(99);
        dto.setPlanId(1);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.createSubscription(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldThrowException_whenPlanNotFound() {
        SubscriptionRequestDTO dto = new SubscriptionRequestDTO();
        dto.setUserId(1);
        dto.setPlanId(99);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(planRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.createSubscription(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Plan not found");
    }

    // === UPDATE ===
    @Test
    void shouldUpdateSubscription_whenValidData() {
        SubscriptionPlan newPlan = new SubscriptionPlan("Pro", "", new BigDecimal("30.0"), 7, 5);
        newPlan.setId(2);

        Subscription subscription = new Subscription(user, plan, now, now.plusDays(7), SubscriptionStatus.ACTIVE,false);

        SubscriptionUpdateDTO dto = new SubscriptionUpdateDTO();
        dto.setSubscriptionId(1);
        dto.setPlanId(2);
        dto.setAutoRenew(false);
        dto.setStatus(SubscriptionStatus.ACTIVE);
        dto.setPaymentMethod(PaymentMethods.CREDIT_CARD);
        dto.setCurrency(CurrencyType.EUR);

        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));
        when(planRepository.findById(2)).thenReturn(Optional.of(newPlan));
        when(paymentRepository.findTopBySubscriptionOrderByPaymentDateDesc(subscription))
                .thenReturn(Optional.of(new Payment(subscription,new BigDecimal("22.22"),CurrencyType.TRY,now,PaymentMethods.CASH,PaymentStatus.SUCCESS)));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription result = subscriptionService.updateSubscription(dto);

        assertThat(result.getPlan().getName()).isEqualTo("Pro");
        assertThat(result.isAutoRenew()).isFalse();
        assertThat(result.getCustomer()).isEqualTo(user);

        verify(paymentRepository).save(any(Payment.class));
    }

    // === DELETE ===
    @Test
    void shouldDeleteSubscription_whenExists() {
        Subscription subscription = new Subscription(user, plan, now, now.plusDays(7), SubscriptionStatus.ACTIVE,false);
        Payment payment = new Payment(subscription,new BigDecimal("22.22"),CurrencyType.TRY,now,PaymentMethods.CASH,PaymentStatus.SUCCESS);

        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));
        when(paymentRepository.findBySubscription(subscription)).thenReturn(List.of(payment));

        subscriptionService.deleteSubscription(1);

        verify(paymentRepository).deleteAll(List.of(payment));
        verify(subscriptionRepository).delete(subscription);
    }

    @Test
    void shouldThrowException_whenSubscriptionNotFoundOnDelete() {
        when(subscriptionRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.deleteSubscription(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Subscription not found");
    }

    // === RENEW ===
    @Test
    void shouldRenewSubscription_whenValid() {
        Subscription subscription = new Subscription(user, plan, now, now.plusDays(7), SubscriptionStatus.ACTIVE,false);

        SubscriptionRenewDTO dto = new SubscriptionRenewDTO();
        dto.setSubscriptionId(1);
        dto.setCurrency(CurrencyType.EUR);
        dto.setPaymentMethod(PaymentMethods.CREDIT_CARD);

        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription result = subscriptionService.renewSubscription(dto);

        assertThat(result.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void shouldThrowException_whenSubscriptionNotFoundOnRenew() {
        SubscriptionRenewDTO dto = new SubscriptionRenewDTO();
        dto.setSubscriptionId(99);

        when(subscriptionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.renewSubscription(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Subscription not found");
    }
}