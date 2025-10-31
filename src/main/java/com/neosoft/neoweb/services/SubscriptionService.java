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

import java.time.LocalDateTime;

/**
 * Service class responsible for managing subscription lifecycle operations.
 * <p>
 * Provides functionality for:
 * <ul>
 *     <li>Creating new subscriptions</li>
 *     <li>Updating existing subscriptions</li>
 *     <li>Deleting subscriptions</li>
 *     <li>Renewing subscriptions</li>
 * </ul>
 */
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

    /**
     * Creates a new subscription and an initial payment record.
     *
     * @param dto Subscription creation request payload
     * @return The saved subscription entity
     */
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
        subscription.setPlan(plan);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setAutoRenew(dto.isAutoRenew());

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Create initial payment record
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

    /**
     * Updates an existing subscription (plan, status, auto-renew, payment info).
     *
     * @param dto Update request payload
     * @return The updated subscription
     */
    @Transactional
    public Subscription updateSubscription(SubscriptionUpdateDTO dto) {

        Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // Update plan if provided
        if (dto.getPlanId() != null) {
            SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Plan not found"));
            subscription.setPlan(plan);
            LocalDateTime startDate = subscription.getStartDate();
            subscription.setEndDate(startDate.plusMonths(plan.getBillingCycle()));
        }

        // Update status
        if (dto.getStatus() != null) {
            subscription.setStatus(dto.getStatus());
        }

        // Update auto-renew
        if (dto.getAutoRenew() != null) {
            subscription.setAutoRenew(dto.getAutoRenew());
        }

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Update latest payment if applicable
        if (dto.getPaymentMethod() != null || dto.getCurrency() != null) {
            Payment payment = paymentRepository
                    .findTopBySubscriptionOrderByPaymentDateDesc(savedSubscription)
                    .orElse(null);

            if (payment != null) {
                if (dto.getPaymentMethod() != null)
                    payment.setPaymentMethod(dto.getPaymentMethod());
                if (dto.getCurrency() != null)
                    payment.setCurrency(dto.getCurrency());
                paymentRepository.save(payment);
            }
        }

        return savedSubscription;
    }

    /**
     * Deletes a subscription and all related payments.
     *
     * @param subscriptionId ID of the subscription to delete
     */
    @Transactional
    public void deleteSubscription(int subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        paymentRepository.deleteAll(paymentRepository.findBySubscription(subscription));
        subscriptionRepository.delete(subscription);
    }

    /**
     * Renews an active or expired subscription and records a new payment.
     *
     * @param dto Renew request payload
     * @return Updated subscription entity
     */
    @Transactional
    public Subscription renewSubscription(SubscriptionRenewDTO dto) {
        Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE &&
                subscription.getStatus() != SubscriptionStatus.EXPIRED) {
            throw new RuntimeException("Subscription cannot be renewed with status: " + subscription.getStatus());
        }

        SubscriptionPlan plan = planRepository.findById(subscription.getPlan().getId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        LocalDateTime newEndDate = subscription.getEndDate().isAfter(LocalDateTime.now())
                ? subscription.getEndDate().plusMonths(plan.getBillingCycle())
                : LocalDateTime.now().plusMonths(plan.getBillingCycle());

        subscription.setEndDate(newEndDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Create renewal payment
        Payment payment = new Payment();
        payment.setSubscription(savedSubscription);
        payment.setAmount(plan.getPrice());
        payment.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : CurrencyType.USD);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : PaymentMethods.CREDIT_CARD);
        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);

        return savedSubscription;
    }
}
