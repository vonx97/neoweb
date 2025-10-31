package com.neosoft.neoweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "billing_cycle", nullable = false)
    private int billingCycle; // Örn: 30 = 30 gün

    @Column(name = "max_users", nullable = false)
    private int maxUsers;

    // === ABONELİKLER (BİR PLANA BAĞLI ÇOK SAYIDA ABONELİK) ===
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // JSON döngüsünü önler
    private List<Subscription> subscriptions = new ArrayList<>();

    // === GETTER / SETTER ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getBillingCycle() { return billingCycle; }
    public void setBillingCycle(int billingCycle) { this.billingCycle = billingCycle; }

    public int getMaxUsers() { return maxUsers; }
    public void setMaxUsers(int maxUsers) { this.maxUsers = maxUsers; }

    public List<Subscription> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<Subscription> subscriptions) { this.subscriptions = subscriptions; }
}
