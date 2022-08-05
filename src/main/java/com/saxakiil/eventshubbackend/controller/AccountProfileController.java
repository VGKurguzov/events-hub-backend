package com.saxakiil.eventshubbackend.controller;

import com.saxakiil.eventshubbackend.auth.MessageResponse;
import com.saxakiil.eventshubbackend.dto.auth.ProfileRequest;
import com.saxakiil.eventshubbackend.model.AccountProfile;
import com.saxakiil.eventshubbackend.model.User;
import com.saxakiil.eventshubbackend.repository.UserRepository;
import com.saxakiil.eventshubbackend.service.UserDetailsImpl;
import com.saxakiil.eventshubbackend.transformer.AccountProfileTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountProfileController {

    private final UserRepository userRepository;
    private final AccountProfileTransformer accountProfileTransformer;

    @Autowired
    public AccountProfileController(final UserRepository userRepository,
                                    final AccountProfileTransformer accountProfileTransformer) {
        this.userRepository = userRepository;
        this.accountProfileTransformer = accountProfileTransformer;
    }

    @GetMapping("/get")
    @PreAuthorize(value = "hasAnyRole('ADMIN', 'ORGANIZE', 'MODERATOR', 'USER')")
    public ResponseEntity<AccountProfile> getProfile() {

        UserDetailsImpl authUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(userRepository.findByUsername(authUser.getUsername()).get().getAccountProfile());
    }

    @PostMapping("/edit")
    @PreAuthorize(value = "hasAnyRole('ADMIN', 'ORGANIZE', 'MODERATOR', 'USER')")
    public ResponseEntity<?> editProfile(@RequestBody ProfileRequest profileRequest) {

        UserDetailsImpl authUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        User user = userRepository.findByUsername(authUser.getUsername()).get();

        AccountProfile updatedAccountProfile = accountProfileTransformer
                .transform(user.getAccountProfile().getId(), profileRequest);
        user.setAccountProfile(updatedAccountProfile);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Account profile is UPDATED"));
    }

    @PostMapping("/edit/{username}")
    @PreAuthorize(value = "hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<?> editProfile(@NonNull @PathVariable String username,
                                         @RequestBody ProfileRequest profileRequest) {

        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username is not exists"));
        }
        User user = userRepository.findByUsername(username).get();

        AccountProfile updatedAccountProfile = accountProfileTransformer
                .transform(user.getAccountProfile().getId(), profileRequest);
        user.setAccountProfile(updatedAccountProfile);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Account profile is UPDATED"));
    }
}
