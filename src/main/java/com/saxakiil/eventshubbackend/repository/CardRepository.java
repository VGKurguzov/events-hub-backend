package com.saxakiil.eventshubbackend.repository;

import com.saxakiil.eventshubbackend.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByPublished(boolean published, Pageable pageable);

    Page<Card> findByTitleContaining(String title, Pageable pageable);
}
