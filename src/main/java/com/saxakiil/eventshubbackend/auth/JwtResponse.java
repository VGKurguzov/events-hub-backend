package com.saxakiil.eventshubbackend.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {

    private String token;
    private Long id;
    final private String type = "Bearer";
    private String username;
    private String email;

}
