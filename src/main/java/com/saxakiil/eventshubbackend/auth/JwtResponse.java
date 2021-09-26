package com.saxakiil.eventshubbackend.auth;

import lombok.*;

import java.util.List;

@Data
@Builder
public class JwtResponse {

    private String token;
    private Long id;
    private String type = "Bearer";
    private String username;
    private String email;
    private List<String> roles;

}
