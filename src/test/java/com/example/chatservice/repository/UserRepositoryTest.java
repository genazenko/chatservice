package com.example.chatservice.repository;

import com.example.chatservice.entity.User;
import com.example.chatservice.repository.impl.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;

    @Test
    void save() {
        userRepository = new InMemoryUserRepository();

        var user = User.builder()
            .username("user")
            .hashedPassword("password".getBytes())
            .build();

        userRepository.save(user);

        var optUser = userRepository.getByUsername(user.getUsername());

        assertTrue(optUser.isPresent());
        assertEquals(user.getHashedPassword(), optUser.get().getHashedPassword());
    }

    @Test
    void shouldNotOverridePassword() {
        userRepository = new InMemoryUserRepository();

        var user = User.builder()
            .username("user")
            .hashedPassword("password".getBytes())
            .build();

        var userWithDiffPass = User.builder()
            .username("user")
            .hashedPassword("password2".getBytes())
            .build();

        userRepository.save(user);
        userRepository.save(userWithDiffPass);

        var optUser = userRepository.getByUsername(user.getUsername());

        assertTrue(optUser.isPresent());
        assertEquals(user.getHashedPassword(), optUser.get().getHashedPassword());
        assertNotEquals(userWithDiffPass.getHashedPassword(), optUser.get().getHashedPassword());
    }
}
