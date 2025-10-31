package com.neosoft.neoweb.dto;

import enums.CurrencyType;
import enums.PaymentMethods;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * SubscriptionRequestDTO
 *
 * Yeni bir abonelik oluşturmak için kullanılan DTO.
 *
 * Alanlar:
 *  - userId: Aboneliği başlatan kullanıcının ID’si
 *  - planId: Seçilen planın ID’si
 *  - paymentMethod: Ödeme yöntemi
 *  - currency: Ödeme para birimi
 *  - autoRenew: Otomatik yenileme (varsayılan true)
 *
 * Validation:
 *  - @NotNull: Zorunlu alanlar
 *  - @Positive: ID değerlerinin pozitif olması gerekir
 */
public class SubscriptionRequestDTO {

    @NotNull(message = "Kullanıcı ID'si belirtilmelidir")
    @Positive(message = "Kullanıcı ID'si pozitif olmalıdır")
    private Integer userId;

    @NotNull(message = "Plan ID'si belirtilmelidir")
    @Positive(message = "Plan ID'si pozitif olmalıdır")
    private Integer planId;

    @NotNull(message = "Ödeme yöntemi belirtilmelidir")
    private PaymentMethods paymentMethod;

    @NotNull(message = "Para birimi belirtilmelidir")
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
