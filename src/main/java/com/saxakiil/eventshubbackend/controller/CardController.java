package com.saxakiil.eventshubbackend.controller;

import com.saxakiil.eventshubbackend.exception.NullableCardException;
import com.saxakiil.eventshubbackend.model.Card;
import com.saxakiil.eventshubbackend.service.CardService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.saxakiil.eventshubbackend.util.Utils.CARD_IS_ADDED;

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
    public ResponseEntity<Card> addElement(@RequestBody Card card) {
        try {
            boolean isAdded = cardService.addNewCard(card);
            if (isAdded) {
                log.info(String.format(CARD_IS_ADDED, card.getId()));
                return new ResponseEntity<>(card, HttpStatus.CREATED);
            } else {
                throw new NullableCardException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
