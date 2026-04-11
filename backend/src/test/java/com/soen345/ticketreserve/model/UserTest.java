package com.soen345.ticketreserve.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    @Test
    void shouldCreateUserAndUseGettersSetters(){
        User user = new User();

        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@example.com");
        user.setPhoneNumber("5141234567");
        user.setPasswordHash("hashed");
        user.setRole("USER");

        assertEquals(1L, user.getId());
        assertEquals("Alex", user.getName());
        assertEquals("alex@example.com", user.getEmail());
        assertEquals("5141234567", user.getPhoneNumber());
        assertEquals("hashed", user.getPasswordHash());
        assertEquals("USER", user.getRole());
    }
}
