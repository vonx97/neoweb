package com.neosoft.neoweb.Repository;


import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.entity.UserSession;
import com.neosoft.neoweb.repository.PaymentRepository;
import com.neosoft.neoweb.repository.UserRepository;
import com.neosoft.neoweb.repository.UserSessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Sadece JPA (veritabanı) katmanını test eder
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserSessionRepositoryTest {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUser()
    {
        User user = new User("testuser","secret","name","surname", LocalDateTime.now());
        userRepository.save(user);

        UserSession session = new UserSession(user,"",LocalDateTime.now().plusDays(7),false,LocalDateTime.now());
        userSessionRepository.save(session);

        Optional<UserSession> found = userSessionRepository.findByUser(user);

        // Assert
        assertThat(found).isPresent(); // Optional boş değil mi?
        assertThat(found.get().getUser().getUsername()).isEqualTo("testuser"); // İçindeki User nesnesini al
    }

}
