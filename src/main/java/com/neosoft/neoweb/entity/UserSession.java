package com.neosoft.neoweb.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(length = 512, unique = true, nullable = false)
    private String refreshToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected UserSession() {}


    public UserSession(User user,String refreshToken, LocalDateTime expiresAt, boolean revoked, LocalDateTime createdAt) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }




    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }

    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public LocalDateTime getExpiresAt() { return expiresAt; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) {this.revoked = revoked;}

    public LocalDateTime getCreatedAt() { return createdAt; }


}
