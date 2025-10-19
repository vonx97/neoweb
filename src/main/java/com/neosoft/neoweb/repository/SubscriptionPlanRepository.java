package com.neosoft.neoweb.repository;

import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan,Integer> {

    Optional<SubscriptionPlan> findByName(String name);
}
