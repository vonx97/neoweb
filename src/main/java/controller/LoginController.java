package controller;

import com.neosoft.neoweb.repository.UserRepository;
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

import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/neoweb")
public class LoginController {


    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {

            User user = userOpt.get();
            if (Neocryptor.verifyStoredPassword(user.getPassword(),request.getPassword())) {

                String accessToken = JwtUtil.generateToken(user.getUsername());
                String refreshToken = UUID.randomUUID().toString();

                user.setRefreshToken(refreshToken); // Refresh token'ı DB'ye yaz
                userRepository.save(user);

                return new LoginResponse(true, "Giriş başarılı", accessToken, refreshToken);

            }

        }


        return new LoginResponse(false, "Kullanıcı adı veya şifre hatalı", null, null);
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
            if (refreshToken.equals(user.getRefreshToken())) {
                String newAccessToken = JwtUtil.generateToken(user.getRefreshToken());
                return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz refresh token");



    }



}
