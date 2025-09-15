package com.example.utmentor.services;

import com.example.utmentor.infrastructures.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class MongoSmoke {
    private final UserRepository users;
    public MongoSmoke(UserRepository users) { this.users = users; }

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void check() {
        System.out.println("Mongo connected. Users count = " + users.count());
    }
}