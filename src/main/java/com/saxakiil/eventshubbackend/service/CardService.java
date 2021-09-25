package com.saxakiil.eventshubbackend.service;

import com.saxakiil.eventshubbackend.model.Card;
import com.saxakiil.eventshubbackend.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(final CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Page<Card> getCardsOnPage(int pageNumber, int pageSize, boolean isPublished) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        return cardRepository.findByPublished(isPublished, paging);
    }

    public boolean addNewCard(Card card) {
        cardRepository.save(card);
        return cardRepository.existsById(card.getId());
    }
}
