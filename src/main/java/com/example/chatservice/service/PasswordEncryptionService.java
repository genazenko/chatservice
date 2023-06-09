package com.example.chatservice.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PasswordEncryptionService {
    private final MessageDigest md;
    private final byte[] salt = "random_salt".getBytes(StandardCharsets.UTF_8);

    public PasswordEncryptionService() throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance("SHA-512");
    }

    public byte[] encryptPassword(String password) {
        md.update(salt);
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }
}
