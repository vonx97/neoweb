package com.neosoft.neoweb.Repository;


import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Sadece JPA (veritabanı) katmanını test eder
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        // Arrange
        User user = new User("testuser","secret","name","surname", LocalDateTime.now());

        // Act
        Optional<User> found = userRepository.findByUsername("testuser");

        // Assert
        assertThat(found).isPresent(); // Optional boş değil mi?
        assertThat(found.get().getUsername()).isEqualTo("testuser"); // İçindeki User nesnesini al
    }


}
