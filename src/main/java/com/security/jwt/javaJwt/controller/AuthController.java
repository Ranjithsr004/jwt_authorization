package com.security.jwt.javaJwt.controller;

import com.security.jwt.javaJwt.dto.RegisterRequest;
import com.security.jwt.javaJwt.entity.Role;
import com.security.jwt.javaJwt.entity.User;
import com.security.jwt.javaJwt.repository.RoleRepository;
import com.security.jwt.javaJwt.repository.UserRepository;
import com.security.jwt.javaJwt.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Register user API
    @PostMapping("/register")  //http:localhost:8080/auth/register
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){

        //check if username already exists
        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        newUser.setPassword(encodedPassword);

        Set<Role> roles = new HashSet<>();
        for(String roleName :  registerRequest.getRoles()){
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }

        newUser.setRoles(roles);
        userRepository.save(newUser);
        return ResponseEntity.ok("User Register Successfully");
    }

    //Login API  - //http:localhost:8080/auth/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword()));
        }
        catch (Exception e){
            System.out.println("Exception: " + e);
        }
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        return ResponseEntity.ok(token);
    }

}
