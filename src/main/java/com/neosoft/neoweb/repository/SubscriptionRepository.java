package com.neosoft.neoweb.repository;

import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.entity.User;
import enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {

    List<Subscription> findByCustomer(User customer);

    List<Subscription> findByStatus(SubscriptionStatus status);

    List<Subscription> findByEndDateBeforeAndStatus(LocalDateTime date, SubscriptionStatus status);


}
