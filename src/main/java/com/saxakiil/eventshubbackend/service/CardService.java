package com.saxakiil.eventshubbackend.service;

import com.saxakiil.eventshubbackend.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

import static com.saxakiil.eventshubbackend.util.Utils.QUERY;

@Service
public class CardService {

    private final EntityManager entityManager;

    @Autowired
    public CardService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Card> getElementsByPage(int pageNumber, int pagerSize) {
        Query query = entityManager.createQuery(QUERY);
        query.setFirstResult((pageNumber - 1) * pagerSize);
        query.setMaxResults(pagerSize);
        return query.getResultList();
    }
}
