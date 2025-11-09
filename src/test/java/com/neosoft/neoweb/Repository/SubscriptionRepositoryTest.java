package com.neosoft.neoweb.Repository;

import com.neosoft.neoweb.entity.SubscriptionPlan;
import com.neosoft.neoweb.repository.SubscriptionPlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Test
    void shouldSaveAndFindPlanById() {
        // Arrange
        SubscriptionPlan plan = new SubscriptionPlan("Basic","", new BigDecimal("9.99"),31,5);
        plan = subscriptionPlanRepository.save(plan);

        // Act
        Optional<SubscriptionPlan> found = subscriptionPlanRepository.findById(plan.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Basic");
        assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("9.99"));
    }
}
