package com.saxakiil.eventshubbackend.controller;

import com.saxakiil.eventshubbackend.model.Card;
import com.saxakiil.eventshubbackend.service.CardService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.saxakiil.eventshubbackend.util.Utils.CARD_IS_ADDED;
import static com.saxakiil.eventshubbackend.util.Utils.CARD_IS_DELETED;

@Slf4j
@RestController
@RequestMapping("/api/card")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(final CardService cardService) {
        this.cardService = cardService;
    }

    @SneakyThrows
    @PostMapping(value = "/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> addElement(@NonNull @RequestBody Card card) {

        boolean isAdded = cardService.addNewCard(card);
        if (isAdded) {
            log.info(String.format(CARD_IS_ADDED, card.getId()));
            return new ResponseEntity<>(card.getId(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping(value = "/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> deleteElement(@NonNull @RequestBody Long id) {

        boolean isDeleted = cardService.deleteById(id);
        if (isDeleted) {
            log.info(String.format(CARD_IS_DELETED, id));
            return new ResponseEntity<>(id, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
}
