package com.neosoft.neoweb.services;

import com.neosoft.neoweb.dto.SubscriptionRenewDTO;
import com.neosoft.neoweb.dto.SubscriptionRequestDTO;
import com.neosoft.neoweb.dto.SubscriptionUpdateDTO;
import com.neosoft.neoweb.entity.Payment;
import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.entity.SubscriptionPlan;
import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.repository.PaymentRepository;
import com.neosoft.neoweb.repository.SubscriptionPlanRepository;
import com.neosoft.neoweb.repository.SubscriptionRepository;
import com.neosoft.neoweb.repository.UserRepository;
import enums.CurrencyType;
import enums.PaymentMethods;
import enums.PaymentStatus;
import enums.SubscriptionStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;
    private final PaymentRepository paymentRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               SubscriptionPlanRepository planRepository,
                               PaymentRepository paymentRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Subscription createSubscription(SubscriptionRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(plan.getBillingCycle());

        Subscription subscription = new Subscription();
        subscription.setCustomer(user);
        subscription.setPlanName(plan.getName());
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setAutoRenew(dto.isAutoRenew());

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        Payment payment = new Payment();
        payment.setSubscription(savedSubscription);
        payment.setAmount(plan.getPrice());
        payment.setCurrency(dto.getCurrency());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);

        return savedSubscription;
    }

    @Transactional
    public Subscription updateSubscription(SubscriptionUpdateDTO dto) {

        Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // Plan değisikliği varsa
        if (dto.getPlanId() != null) {
            SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Plan not found"));
            subscription.setPlanName(plan.getName());
            // startDate ve endDate'ı yeniden ayarlayabilirsin
            LocalDateTime startDate = subscription.getStartDate();
            subscription.setEndDate(startDate.plusMonths(plan.getBillingCycle()));
        }

        // Status güncelleme
        if (dto.getStatus() != null) {
            subscription.setStatus(dto.getStatus());
        }

        // Auto-renew güncelleme
        if (dto.getAutoRenew() != null) {
            subscription.setAutoRenew(dto.getAutoRenew());
        }

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Ödeme güncelleme opsiyonel
        if (dto.getPaymentMethod() != null || dto.getCurrency() != null) {
            // son ödeme kaydını al
            Payment payment = paymentRepository.findBySubscription(savedSubscription)
                    .stream().reduce((first, second) -> second).orElse(null);

            if (payment != null) {
                if (dto.getPaymentMethod() != null) payment.setPaymentMethod(dto.getPaymentMethod());
                if (dto.getCurrency() != null) payment.setCurrency(dto.getCurrency());
                paymentRepository.save(payment);
            }
        }

        return savedSubscription;
    }

    @Transactional
    public void deleteSubscription(int subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // İliskili odemeleri sil
        paymentRepository.deleteAll(paymentRepository.findBySubscription(subscription));

        subscriptionRepository.delete(subscription);
    }

    @Transactional
    public Subscription renewSubscription(SubscriptionRenewDTO dto) {
        // 1️⃣ Aboneliği bul
        Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // 2️⃣ Abonelik durumu kontrolü
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE &&
                subscription.getStatus() != SubscriptionStatus.EXPIRED) {
            throw new RuntimeException("Subscription cannot be renewed with status: " + subscription.getStatus());
        }

        // 3️⃣ Plan bilgilerini al
        SubscriptionPlan plan = planRepository.findByName(subscription.getPlanName())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // 4️⃣ Yeni endDate hesapla
        LocalDateTime newEndDate = subscription.getEndDate().isAfter(LocalDateTime.now())
                ? subscription.getEndDate().plusMonths(plan.getBillingCycle())
                : LocalDateTime.now().plusMonths(plan.getBillingCycle());

        subscription.setEndDate(newEndDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        // 5️⃣ Aboneliği kaydet
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // 6️⃣ Yeni ödeme kaydı oluştur
        Payment payment = new Payment();
        payment.setSubscription(savedSubscription);
        payment.setAmount(plan.getPrice());
        payment.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : CurrencyType.USD);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : PaymentMethods.CREDIT_CARD);
        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);

        // 7️⃣ Güncellenmiş aboneliği döndür
        return savedSubscription;
    }



}
