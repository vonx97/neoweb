package com.neosoft.neoweb.dto;

import enums.CurrencyType;
import enums.PaymentMethods;

public class SubscriptionRenewDTO {

    private int subscriptionId;            // Uzatılacak abonelik ID'si
    private PaymentMethods paymentMethod;  // Ödeme yöntemi
    private CurrencyType currency;         // Ödeme para birimi

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
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
