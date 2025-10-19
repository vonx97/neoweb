package controller;

import com.neosoft.neoweb.entity.Role;
import com.neosoft.neoweb.entity.UserSession;
import com.neosoft.neoweb.repository.UserRepository;
import com.neosoft.neoweb.repository.UserSessionRepository;
import com.neosoft.neoweb.entity.User;
import com.neosoft.neoweb.model.LoginRequest;
import com.neosoft.neoweb.model.LoginResponse;
import com.neosoft.neoweb.security.JwtUtil;
import com.vonx98.Neocryptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/neoweb")
public class LoginController {


    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;

    public LoginController(UserRepository userRepository, UserSessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {

            User user = userOpt.get();
            if (Neocryptor.verifyStoredPassword(user.getPassword(),request.getPassword())) {

                String accessToken = JwtUtil.generateToken(user.getUsername(),
                        user.getRoles().stream().map(Role::getName).toList());
                String refreshToken = JwtUtil.generateRefreshToken(user.getUsername(),604800000);

                // UserSession guncelle / olustur
                UserSession session = sessionRepository.findByUser(user)
                        .orElseGet(() -> {
                            UserSession s = new UserSession();
                            s.setUser(user);
                            return s;
                        });

                session.setRefreshToken(refreshToken);
                session.setLastLoginDate(LocalDateTime.now());
                sessionRepository.save(session);

                return new LoginResponse(true, "Giriş başarılı", accessToken, refreshToken,user.getRoles().stream().map(Role::getName).toList());

            }

        }


        return new LoginResponse(false, "Kullanıcı adı veya şifre hatalı", null, null,null);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String,String> body) {

        String username = body.get("username");
        String refreshToken = body.get("refreshToken");

        if (username == null || refreshToken == null) {
            return ResponseEntity.badRequest().body("Eksik parametre");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<UserSession> sessionOpt = sessionRepository.findByUserAndRefreshToken(user, refreshToken);

            if (sessionOpt.isPresent()) {
                String newAccessToken = JwtUtil.generateToken(user.getUsername(),user.getRoles().stream().map(Role::getName).toList());
                return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
            }

        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz refresh token");



    }



}
