package com.example.chatservice.repository.impl;

import com.example.chatservice.entity.User;
import com.example.chatservice.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentMap<String, User> userStore = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        userStore.putIfAbsent(user.getUsername(), user);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return Optional.ofNullable(userStore.get(username));
    }
}
