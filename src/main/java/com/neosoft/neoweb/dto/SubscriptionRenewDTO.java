package com.neosoft.neoweb.dto;

import enums.CurrencyType;
import enums.PaymentMethods;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


/**
 * SubscriptionRenewDTO
 *
 * Abonelik yenileme işlemleri için istenen DTO.
 * Bu sınıf, client tarafından /subscriptions/renew endpoint'ine gönderilir.
 *
 * Alanlar:
 *  - subscriptionId: Yenilenecek aboneliğin ID'si (pozitif olmalı)
 *  - paymentMethod: Ödeme yöntemi (zorunlu)
 *  - currency: Ödeme para birimi (zorunlu)
 *
 * Validation:
 *  @Min(1): ID'nin 1'den büyük olması gerekir.
 *  @NotNull: Boş (null) gönderilemez.
 */
public class SubscriptionRenewDTO {

    @Min(value = 1, message = "Abonelik ID'si 1 veya daha büyük olmalıdır")
    private int subscriptionId; // Uzatılacak abonelik ID’si

    @NotNull(message = "Ödeme yöntemi belirtilmelidir")
    private PaymentMethods paymentMethod; // Ödeme yöntemi

    @NotNull(message = "Para birimi belirtilmelidir")
    private CurrencyType currency; // Ödeme para birimi

    // --- Getters & Setters ---
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
