package controller;

import com.neosoft.neoweb.model.LoginRequest;
import com.neosoft.neoweb.model.LoginResponse;
import com.neosoft.neoweb.security.JwtUtil;
import com.vonx98.Neocryptor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/neoweb")
public class LoginController {

    private final Set<String> validUsers = ConcurrentHashMap.newKeySet();

    public LoginController() {
        // Örnek kullanıcılar
        validUsers.add("user1:1234");
        validUsers.add("user2:abcd");
        validUsers.add("admin:admin");
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String key = request.getUsername() + ":" + request.getPassword();
        if (validUsers.contains(key)) {

            String cryptedPassword = Neocryptor.hashPassword(request.getPassword());
            String token = JwtUtil.generateToken(request.getUsername());
            return new LoginResponse(true, "Giriş başarılı!",token);
        } else {
            return new LoginResponse(false, "Kullanıcı adı veya şifre hatalı",null);
        }
    }



}
