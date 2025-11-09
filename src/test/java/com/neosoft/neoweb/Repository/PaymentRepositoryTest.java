package com.neosoft.neoweb.Repository;


import com.neosoft.neoweb.entity.Payment;
import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.repository.PaymentRepository;
import com.neosoft.neoweb.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindPaymentById() {
        User user = new User("testuser", "secret", "name", "surname", LocalDateTime.now());
        userRepository.save(user);


    }
}
