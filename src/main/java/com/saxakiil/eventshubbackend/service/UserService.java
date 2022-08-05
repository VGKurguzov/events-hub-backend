package com.saxakiil.eventshubbackend.service;

import com.saxakiil.eventshubbackend.model.Card;
import com.saxakiil.eventshubbackend.model.User;
import com.saxakiil.eventshubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    public boolean addOrganizeInfo(Long organizeId, Card card) {
        if (userRepository.findById(organizeId).isPresent()) {
            User organize = userRepository.findById(organizeId).get();
            organize.getOrganizeCard().add(card);
            userRepository.save(organize);
            return true;
        }
        return false;
    }

    public boolean addFavoriteCard(Long userId, Card card) {
        if (userRepository.findById(userId).isPresent()) {
            User user = userRepository.findById(userId).get();
            if (user.getFavoriteCards().contains(card)) {
                return false;
            }
            user.getFavoriteCards().add(card);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
