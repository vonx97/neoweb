package com.neosoft.neoweb.Repository;


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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionPlanRepository planRepository;

    @Test
    void shouldSaveAndFindPaymentById() {
        // 1. Plan oluştur ve kaydet
        SubscriptionPlan plan = new SubscriptionPlan("Basic Plan","", BigDecimal.valueOf(99.99),31,5);
        plan = planRepository.save(plan);

        // 2. User oluştur ve kaydet
        User user = new User("payer", "secret", "Name", "Surname", LocalDateTime.now());
        user = userRepository.save(user);

        // 3. Subscription oluştur ve kaydet
        Subscription subscription = new Subscription(user, plan, LocalDateTime.now(), LocalDateTime.now().plusMonths(1), SubscriptionStatus.ACTIVE,false);
        subscription = subscriptionRepository.save(subscription);

        // 4. Payment oluştur ve kaydet
        Payment payment = new Payment(subscription, new BigDecimal("100.00"),CurrencyType.TRY,LocalDateTime.now(), PaymentMethods.CASH, PaymentStatus.SUCCESS);
        payment = paymentRepository.save(payment);


        // 5. Test et
        Optional<Payment> found = paymentRepository.findById(payment.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSubscription().getCustomer().getUsername()).isEqualTo("payer");
        assertThat(found.get().getSubscription().getPlan().getName()).isEqualTo("Basic Plan");
        assertThat(found.get().getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldFindPaymentsBySubscription() {
        SubscriptionPlan plan = planRepository.save(new SubscriptionPlan("Pro Plan","", BigDecimal.valueOf(199.99),31,5));
        User user = userRepository.save(new User("proUser", "secret", "Name", "Surname", LocalDateTime.now()));
        Subscription subscription = subscriptionRepository.save(new Subscription(user, plan, LocalDateTime.now(), LocalDateTime.now().plusMonths(1),SubscriptionStatus.ACTIVE,false));

        Payment p1 = paymentRepository.save(new Payment(subscription, new BigDecimal("200.00"),CurrencyType.TRY,LocalDateTime.now(),PaymentMethods.CASH,PaymentStatus.SUCCESS));
        Payment p2 = paymentRepository.save(new Payment(subscription, new BigDecimal("200.00"),CurrencyType.TRY,LocalDateTime.now(),PaymentMethods.CREDIT_CARD,PaymentStatus.SUCCESS));

        // findBySubscription
        List<Payment> payments = paymentRepository.findBySubscription(subscription);
        assertThat(payments).hasSize(2);
        assertThat(payments).extracting("id").containsExactlyInAnyOrder(p1.getId(), p2.getId());

        // findTopBySubscriptionOrderByPaymentDateDesc
        Optional<Payment> latestPayment = paymentRepository.findTopBySubscriptionOrderByPaymentDateDesc(subscription);
        assertThat(latestPayment).isPresent();
        assertThat(latestPayment.get().getId()).isEqualTo(p2.getId()); // en son ödeme
    }
}
