package com.example.wishservice.service;

import com.example.wishservice.model.Wish;
import com.example.wishservice.repository.WishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WishService {

    @Autowired
    private WishRepository wishRepository;

    public Wish createWish(Wish wish) {
        return wishRepository.save(wish);
    }

    public Wish getWish(String id) {
        return wishRepository.findById(id).orElse(null);
    }

    public Wish updateWish(String id, Wish wish) {
        Wish existingWish = wishRepository.findById(id).orElse(null);
        if (existingWish != null) {
            existingWish.setName(wish.getName());
            existingWish.setDescription(wish.getDescription());
            existingWish.setStatus(wish.getStatus());
            return wishRepository.save(existingWish);
        }
        return null;
    }

    public void updateWishStatus(String id, String newStatus) {
        Wish existingWish = wishRepository.findById(id).orElse(null);
        if (existingWish != null) {
            switch (newStatus) {
                case "in Bearbeitung":
                    if ("nur formuliert".equals(existingWish.getStatus())) {
                        existingWish.setStatus(newStatus);
                    } else {
                        throw new IllegalStateException("Der Wunsch muss 'nur formuliert' sein, um 'in Bearbeitung' gesetzt zu werden.");
                    }
                    break;
                case "in Auslieferung":
                    if ("in Bearbeitung".equals(existingWish.getStatus())) {
                        existingWish.setStatus(newStatus);
                    } else {
                        throw new IllegalStateException("Der Wunsch muss 'in Bearbeitung' sein, um 'in Auslieferung' gesetzt zu werden.");
                    }
                    break;
                case "unter dem Weihnachtsbaum":
                    if ("in Auslieferung".equals(existingWish.getStatus())) {
                        existingWish.setStatus(newStatus);
                    } else {
                        throw new IllegalStateException("Der Wunsch muss 'in Auslieferung' sein, um 'unter dem Weihnachtsbaum' gesetzt zu werden.");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Ungültiger Status: " + newStatus);
            }
            wishRepository.save(existingWish);
        } else {
            throw new NoSuchElementException("Wunsch mit ID " + id + " nicht gefunden.");
        }
    }



    public void deleteWish(String id) {
        wishRepository.deleteById(id);
    }

    public List<Wish> getAllWishes() {
        return wishRepository.findAll();
    }
}
