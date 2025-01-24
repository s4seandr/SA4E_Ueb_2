package com.example.wishservice.controller;

import com.example.wishservice.model.Wish;
import com.example.wishservice.service.WishService;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/wishes")
public class WishController {

    @Autowired
    private WishService wishService;

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping
    public Wish createWish(@RequestBody Wish wish) {
        return wishService.createWish(wish);
    }

    @GetMapping("/{id}")
    public Wish getWish(@PathVariable String id) {
        return wishService.getWish(id);
    }

    @PutMapping("/{id}")
    public Wish updateWish(@PathVariable String id, @RequestBody Wish wish) {
        return wishService.updateWish(id, wish);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateWishStatus(@PathVariable String id, @RequestBody String status) {
        try {
            status = status.replace("\"", "");
            wishService.updateWishStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteWish(@PathVariable String id) {
        wishService.deleteWish(id);
    }

    @GetMapping
    public List<Wish> getAllWishes() {
        return wishService.getAllWishes();
    }

    @GetMapping("/startFileMove")
    public String startFileMove() {
        producerTemplate.sendBody("file:src/main/resources/inbox?noop=false", "Camel_Example.txt"); // Trigger the Camel route with sample content
        return "File move process started!";
    }
}
