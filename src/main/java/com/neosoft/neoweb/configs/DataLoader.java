package com.neosoft.neoweb.configs;

import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.repository.UserRepository;
import enums.RoleName;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.findByUsername("testuser").isEmpty()) {
            User user = new User("testuser",passwordEncoder.encode("password123"),"mehmet","KILIÇ", LocalDateTime.now());
            user.setRoles(Set.of(RoleName.ADMIN,RoleName.USER));
            userRepository.save(user);
        }
    }

}
