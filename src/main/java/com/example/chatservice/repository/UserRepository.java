package com.example.chatservice.repository;

import com.example.chatservice.entity.User;

import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> getByUsername(String username);
}
