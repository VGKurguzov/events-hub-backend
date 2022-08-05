package com.saxakiil.eventshubbackend.controller;

import com.saxakiil.eventshubbackend.model.Card;
import com.saxakiil.eventshubbackend.service.CardService;
import com.saxakiil.eventshubbackend.service.UserDetailsImpl;
import com.saxakiil.eventshubbackend.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.saxakiil.eventshubbackend.util.Utils.*;

@Slf4j
@RestController
@RequestMapping("/api/card")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @Autowired
    public CardController(final CardService cardService, final UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @SneakyThrows
    @PostMapping(value = "/add")
    @PreAuthorize(value = "hasAnyRole('ORGANIZE')")
    public ResponseEntity<Long> addElement(@NonNull @RequestBody Card card) {
        UserDetailsImpl organization = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal());

        if (userService.getById(organization.getId()).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        card.setUserOrganizeId(organization.getId());
        Card updatedCard = cardService.addNewCard(card);
        if (updatedCard.getId() != null) {
            boolean isUpdated = userService.addOrganizeInfo(organization.getId(), card);
            if (isUpdated) {
                log.info(String.format(CARD_IS_ADDED, updatedCard.getId()));
                return new ResponseEntity<>(updatedCard.getId(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @SneakyThrows
    @PutMapping(value = "/activateById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Long> publishElement(@NonNull @PathVariable Long id) {
        if (cardService.publishByID(id)) {
            log.info(String.format(CARD_IS_PUBLISHED, id));
            return new ResponseEntity<>(id, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Long> deleteElement(@NonNull @PathVariable Long id) {

        boolean isDeleted = cardService.deleteById(id);
        if (isDeleted) {
            log.info(String.format(CARD_IS_DELETED, id));
            return new ResponseEntity<>(id, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping(value = "/get")
    public ResponseEntity<Card> getElement(@NonNull @RequestParam Long id) {
        if (cardService.getById(id).isPresent()) {
            return new ResponseEntity<>(cardService.getById(id).get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
