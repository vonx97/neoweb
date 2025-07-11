package com.neosoft.neoweb.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // API için CSRF kapalı
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/neoweb/login").permitAll()  // login endpoint açık
                        .anyRequest().authenticated()               // diğerleri giriş ister
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // session yok


        return http.build();
    }

    // Eğer istersen burada jwtAuthenticationFilter() bean’i tanımlayabilirsin
}