package com.neosoft.neoweb.entity;

import enums.CurrencyType;
import enums.PaymentMethods;
import enums.PaymentStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Abonelik ile ilişki (bir aboneliğin birden fazla ödemesi olabilir)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethods paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    protected Payment() {

    }


    public Payment(Subscription subscription, BigDecimal amount, CurrencyType currency, LocalDateTime paymentDate, PaymentMethods paymentMethod, PaymentStatus status) {
        this.subscription = subscription;
        this.amount = amount;
        this.currency = currency;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;

    }


    // === GETTERS & SETTERS ===

    public int getId() { return id; }

    public Subscription getSubscription() { return subscription; }

    public BigDecimal getAmount() { return amount; }

    public CurrencyType getCurrency() { return currency; }
    public void setCurrency(CurrencyType currency) { this.currency = currency; }

    public LocalDateTime getPaymentDate() { return paymentDate; }

    public PaymentMethods getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethods paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

}
