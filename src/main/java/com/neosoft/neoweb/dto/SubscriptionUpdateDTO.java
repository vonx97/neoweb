package com.neosoft.neoweb.dto;

import enums.CurrencyType;
import enums.PaymentMethods;
import enums.SubscriptionStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * SubscriptionUpdateDTO
 *
 * Bir aboneliği güncellemek için kullanılır.
 * Bazı alanlar opsiyoneldir (örneğin: planId, paymentMethod, currency),
 * ancak subscriptionId zorunludur.
 */
public class SubscriptionUpdateDTO {

    @NotNull(message = "Abonelik ID'si belirtilmelidir")
    @Positive(message = "Abonelik ID'si pozitif olmalıdır")
    private Integer subscriptionId;

    private Integer planId; // opsiyonel: farklı bir plana geçiş yapılabilir
    private SubscriptionStatus status; // opsiyonel: ACTIVE, PAUSED, CANCELED vb.
    private Boolean autoRenew; // opsiyonel: otomatik yenileme açık/kapalı
    private PaymentMethods paymentMethod; // opsiyonel: yeni ödeme yöntemi
    private CurrencyType currency; // opsiyonel: farklı para birimi

    // Getters & Setters
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
