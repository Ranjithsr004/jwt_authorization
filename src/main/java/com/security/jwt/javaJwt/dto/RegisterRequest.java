package com.security.jwt.javaJwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String username;
    private String password;
    private Set<String> roles;  // A set of role names to be passed in the request
}
