package com.neosoft.neoweb.repository;

import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByUser(User user);

    Optional<UserSession> findByUserAndRefreshToken(User user, String refreshToken);


}
