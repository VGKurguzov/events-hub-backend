package com.saxakiil.eventshubbackend.controller;

import com.saxakiil.eventshubbackend.auth.JwtResponse;
import com.saxakiil.eventshubbackend.auth.LoginRequest;
import com.saxakiil.eventshubbackend.auth.MessageResponse;
import com.saxakiil.eventshubbackend.auth.SignupRequest;
import com.saxakiil.eventshubbackend.config.jwt.JwtUtils;
import com.saxakiil.eventshubbackend.model.EnumRole;
import com.saxakiil.eventshubbackend.model.Role;
import com.saxakiil.eventshubbackend.model.User;
import com.saxakiil.eventshubbackend.repository.RoleRepository;
import com.saxakiil.eventshubbackend.repository.UserRepository;
import com.saxakiil.eventshubbackend.service.UserDetailsImpl;
import com.saxakiil.eventshubbackend.util.RoleNormalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final String secret;

    @Autowired
    public AuthController(final AuthenticationManager authenticationManager,
                          final UserRepository userRepository,
                          final RoleRepository roleRepository,
                          final PasswordEncoder passwordEncoder,
                          final JwtUtils jwtUtils,
                          final @Value("${app.adminSecret}") String secret) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.secret = secret;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .build());
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is exist"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is exist"));
        }

        Set<String> reqRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (reqRoles.stream().anyMatch(role -> role.contains("admin")) &&
                !secret.equals(signupRequest.getAdminSecret())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid secret"));
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();

        reqRoles.forEach(requestRole -> Arrays.stream(EnumRole.values()).forEach(role -> {
            if (requestRole.contains(RoleNormalizer.normalize(role.name()))) {
                Role checkedRole = roleRepository
                        .findByName(role)
                        .orElseThrow(() -> new RuntimeException("Error, Role is not found"));
                roles.add(checkedRole);
            }
        }));
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User CREATED"));
    }
}
