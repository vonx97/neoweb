package com.neosoft.neoweb.dto;

import com.sun.istack.NotNull;
import enums.CurrencyType;
import enums.PaymentMethods;



public class SubscriptionRequestDTO {

    @NotNull
    private Integer userId;

    @NotNull
    private Integer planId;

    @NotNull
    private PaymentMethods paymentMethod;

    @NotNull
    private CurrencyType currency;

    private boolean autoRenew = true; // opsiyonel, default true

    // Getters & Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

    public PaymentMethods getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethods paymentMethod) { this.paymentMethod = paymentMethod; }

    public CurrencyType getCurrency() { return currency; }
    public void setCurrency(CurrencyType currency) { this.currency = currency; }

    public boolean isAutoRenew() { return autoRenew; }
    public void setAutoRenew(boolean autoRenew) { this.autoRenew = autoRenew; }
}
