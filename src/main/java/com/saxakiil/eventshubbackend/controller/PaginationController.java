package com.saxakiil.eventshubbackend.controller;

import com.saxakiil.eventshubbackend.model.Card;
import com.saxakiil.eventshubbackend.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.saxakiil.eventshubbackend.util.Utils.PAGE_SIZE;

@RestController
@RequestMapping("/pagination")
public class PaginationController {

    private final CardService cardService;

    @Autowired
    public PaginationController(final CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping(value = "/getElements")
    public ResponseEntity<List<Card>> getElements(
            @RequestParam Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        final List<Card> cards = cardService.getElementsByPage(pageNumber, pageSize == null ? PAGE_SIZE : pageSize);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
