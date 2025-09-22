package com.neosoft.neoweb.dto;

import enums.CurrencyType;
import enums.PaymentMethods;
import enums.SubscriptionStatus;

public class SubscriptionUpdateDTO {

    private Integer subscriptionId; // hangi abonelik güncellenecek
    private Integer planId;          // opsiyonel yeni plan
    private SubscriptionStatus status;
    private Boolean autoRenew;       // opsiyonel
    private PaymentMethods paymentMethod; // opsiyonel ödeme değişimi
    private CurrencyType currency;        // opsiyonel ödeme değişimi


    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public PaymentMethods getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethods paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }
}
