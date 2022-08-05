package com.saxakiil.eventshubbackend.controller;

import com.saxakiil.eventshubbackend.auth.MessageResponse;
import com.saxakiil.eventshubbackend.dto.profile.OrganizeProfileResponse;
import com.saxakiil.eventshubbackend.model.Card;
import com.saxakiil.eventshubbackend.model.EnumRole;
import com.saxakiil.eventshubbackend.model.User;
import com.saxakiil.eventshubbackend.service.CardService;
import com.saxakiil.eventshubbackend.service.UserDetailsImpl;
import com.saxakiil.eventshubbackend.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @Autowired
    public UserController(final UserService userService,
                          final CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @GetMapping("/getOrganizationById")
    public ResponseEntity<?> getOrganizationById(@NonNull @RequestParam Long id) {

        if (userService.getById(id).isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(String.format("Error: User with id='%s' not found", id)));
        }

        User organize = userService.getById(id).get();

        OrganizeProfileResponse organizeProfileResponse = OrganizeProfileResponse.builder()
                .username(organize.getUsername())
                .email(organize.getEmail())
                .phoneNumber(organize.getAccountProfile().getPhoneNumber())
                .address(organize.getAccountProfile().getAddress())
                .urlOnSite(organize.getAccountProfile().getUrlOnSite())
                .build();

        return organize.getRole().getName().equals(EnumRole.ROLE_ORGANIZE) ?
                ResponseEntity.ok(organizeProfileResponse) : ResponseEntity.badRequest()
                .body(new MessageResponse("Error: This user is not organization"));
    }

    @SneakyThrows
    @GetMapping(value = "/getOrganizationCardByUsername")
    public ResponseEntity<Set<Card>> getOrganizationCardByUsername(@NonNull @RequestParam String username) {
        if (userService.getByUsername(username).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Set<Card> cardSet = userService.getByUsername(username).get().getOrganizeCard();
        return new ResponseEntity<>(cardSet, HttpStatus.OK);
    }

    @PostMapping("/likeCardById/{id}")
    @PreAuthorize(value = "hasAnyRole('USER')")
    public ResponseEntity<?> likeCardById(@NonNull @PathVariable Long id) {
        UserDetailsImpl authUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (cardService.getById(id).isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(String.format("Error: Card with id='%s' not exist", id)));
        }

        if (userService.getById(authUser.getId()).isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(String.format("Error: User with id='%s' not exist", authUser.getId())));
        }

        Card card = cardService.getById(id).get();
        User user = userService.getById(authUser.getId()).get();

        return cardService.addUserInLikedList(user, card.getId()) && userService.addFavoriteCard(user.getId(), card)
                ? ResponseEntity.ok().body(authUser.getId()) :
                ResponseEntity.badRequest().body(new MessageResponse("Error: Card is not added"));
    }

    @GetMapping("/getFavoriteCards")
    @PreAuthorize(value = "hasAnyRole('USER')")
    public ResponseEntity<Set<Card>> getFavoriteCards() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Set<Card> favoriteCards = userService.getById(user.getId()).get().getFavoriteCards();
        return ResponseEntity.ok().body(favoriteCards);
    }
}
