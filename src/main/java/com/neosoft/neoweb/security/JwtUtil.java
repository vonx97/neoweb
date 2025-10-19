package com.neosoft.neoweb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUtil {



    private static final String SECRET_KEY = "buCokGucluVeEnAz32ByteUzunlugundaBirSecretKey!";
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private static final long validityInMs  = 60 * 60 * 1000; //60dk


    public static String generateToken(String username,List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles",roles.stream().map(r->"ROLE_"+r).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityInMs)) // 1 saat geçerli
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }




    public static String generateRefreshToken(String username, long expirationMs) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public static boolean validateToken(String token, String username) {
        String extractedUsername = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return extractedUsername.equals(username);
    }

    public static String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("roles", List.class); // JSON array’i List<String> olarak alır
    }


    public static String validateTokenAndGetUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}
