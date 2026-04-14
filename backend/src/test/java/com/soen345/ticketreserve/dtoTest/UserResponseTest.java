package com.soen345.ticketreserve.dtoTest;

import com.soen345.ticketreserve.dto.UserResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserResponseTest {

    @Test
    void shouldCreateUserResponseAndUseGettersSetters() {
        UserResponse response = new UserResponse();

        response.setId(7L);
        response.setName("Alex");
        response.setEmail("alex@example.com");
        response.setPhoneNumber("5141234567");
        response.setRole("HOST");

        assertEquals(7L, response.getId());
        assertEquals("Alex", response.getName());
        assertEquals("alex@example.com", response.getEmail());
        assertEquals("5141234567", response.getPhoneNumber());
        assertEquals("HOST", response.getRole());
    }

    @Test
    void shouldCreateUserResponseWithConstructor() {
        UserResponse response = new UserResponse(11L, "Morgan", "morgan@example.com", "4385550000", "CUSTOMER");

        assertEquals(11L, response.getId());
        assertEquals("Morgan", response.getName());
        assertEquals("morgan@example.com", response.getEmail());
        assertEquals("4385550000", response.getPhoneNumber());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void shouldDefaultFieldsToNullWithNoArgConstructor() {
        UserResponse response = new UserResponse();

        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertNull(response.getPhoneNumber());
        assertNull(response.getRole());
    }
}
