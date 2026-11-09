package com.security.jwt.javaJwt.security;

import com.security.jwt.javaJwt.entity.Role;
import com.security.jwt.javaJwt.entity.User;
import com.security.jwt.javaJwt.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final long jwtExpirationMs = 36000;

    private UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Generate Token
    public String generateToken(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Set<Role> roles = user.get().getRoles();

        return Jwts.builder().setSubject(username).claim("roles", roles.stream()
                        .map(role -> role.getName()).collect(Collectors.joining(",")))
                .setIssuedAt(new Date()).setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(secretKey).compact();
    }

    // Extract username
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Exract roles
    public Set<String> extractRoles(String token) {
        String rolesString = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody()
                .get("roles", String.class);
        return Set.of(rolesString.split(","));
    }

    // Validate token
    public boolean isTokenVaild(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
