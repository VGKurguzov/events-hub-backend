package com.saxakiil.eventshubbackend.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "image_url")
    private String imageUrl;

    private String title;

    private String description;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "url_on_event")
    private String urlOnEvent;

    @Column(name = "is_published")
    private boolean published;
}
