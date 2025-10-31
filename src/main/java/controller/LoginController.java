package controller;

import com.neosoft.neoweb.entity.UserSession;
import com.neosoft.neoweb.repository.UserRepository;
import com.neosoft.neoweb.repository.UserSessionRepository;
import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.model.LoginRequest;
import com.neosoft.neoweb.model.LoginResponse;
import com.neosoft.neoweb.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * LoginController
 *
 * Sağladığı endpoint'ler:
 *  - POST /neoweb/login : Kullanıcı doğrulama, access token ve DB-tabanlı refresh token oluşturma.
 *  - POST /neoweb/refresh-token : Refresh token doğrulama, yeni access token üretme.
 *
 * Tasarım notları:
 *  - Access token: Kısa ömürlü JWT (stateless).
 *  - Refresh token: DB'de saklanan UUID (stateful), revocation + expiry ile yönetilir.
 *  - refresh-token endpoint'ine asla client'tan username alınmaz; refreshToken DB'den doğrudan çözülür.
 */
@RestController
@RequestMapping("/neoweb")
public class LoginController {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository,
                           UserSessionRepository sessionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Kullanıcı adı veya şifre hatalı"));
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Kullanıcı adı veya şifre hatalı"));
        }

        // eski oturumları iptal et
        sessionRepository.findByUser(user).ifPresent(s -> {
            s.setRevoked(true);
            sessionRepository.save(s);
        });

        String refreshToken = UUID.randomUUID().toString();

        UserSession session = new UserSession();
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        session.setRevoked(false);
        sessionRepository.save(session);

        // enum isimlerini string olarak al
        var roles = user.getRoles().stream().map(Enum::name).toList();

        String accessToken = JwtUtil.generateAccessToken(user.getUsername(), roles);

        return ResponseEntity.ok(new LoginResponse(true, "Giriş başarılı", accessToken, refreshToken, roles));
    }

}

