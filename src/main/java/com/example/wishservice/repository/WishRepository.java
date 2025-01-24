package com.example.wishservice.repository;

import com.example.wishservice.model.Wish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishRepository extends MongoRepository<Wish, String> {
}
