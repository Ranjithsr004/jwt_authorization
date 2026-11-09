package com.security.jwt.javaJwt.controller;


import com.security.jwt.javaJwt.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${role.admin}")
    private String roleAdmin;

    @Value("${role.user}")
    private String roleUser;

    @GetMapping("/protected-data")
    public ResponseEntity<String> getProtectedData(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);

            try {
                if (jwtUtil.isTokenVaild(jwtToken)) {
                    String username = jwtUtil.extractUsername(jwtToken);

                    Set<String> roles = jwtUtil.extractRoles(jwtToken);

                    if (roles.contains(roleAdmin)) {
                        return ResponseEntity.ok("Welcome " + username + ", Here is the " + roles + "-specific data.");
                    } else if (roles.contains(roleUser)) {
                        return ResponseEntity.ok("Welcome " + username + ", Here is the " + roles + "-specific data.");
                    } else {
                        return ResponseEntity.status(403).body("Access Denied: You don't have the necessary role");
                    }
                }
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing or invalid");
    }
}
