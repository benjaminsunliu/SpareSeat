package com.soen345.ticketreserve.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void shouldCreateUserWithConstructor() {
        User user = new User("Taylor", "taylor@example.com", "4385551111", "HOST");

        assertNull(user.getId());
        assertEquals("Taylor", user.getName());
        assertEquals("taylor@example.com", user.getEmail());
        assertEquals("4385551111", user.getPhoneNumber());
        assertEquals("HOST", user.getRole());
        assertNull(user.getPasswordHash());
    }
}
