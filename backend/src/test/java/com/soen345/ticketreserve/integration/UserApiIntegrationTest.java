package com.soen345.ticketreserve.integration;

import com.soen345.ticketreserve.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserApiIntegrationTest extends IntegrationTestSupport {

    @Test
    void shouldRegisterAndLoginCustomerWithEmail() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Alice",
                                  "email": "alice@example.com",
                                  "password": "Password1",
                                  "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        User savedUser = userRepository.findByEmail("alice@example.com").orElseThrow();
        assertNotNull(savedUser.getId());
        assertEquals("Alice", savedUser.getName());
        assertTrue(passwordEncoder.matches("Password1", savedUser.getPasswordHash()));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "alice@example.com",
                                  "password": "Password1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void shouldRejectDuplicateEmailRegistrationIgnoringCase() throws Exception {
        createUser("Existing User", "taken@example.com", null, "Password1", "CUSTOMER");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Another User",
                                  "email": "TAKEN@example.com",
                                  "password": "Password1",
                                  "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already registered"));
    }

    @Test
    void shouldRegisterAndLoginHostWithPhoneNumber() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Morgan",
                                  "phoneNumber": "5145551234",
                                  "password": "Password1",
                                  "role": "HOST"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Morgan"))
                .andExpect(jsonPath("$.phoneNumber").value("5145551234"))
                .andExpect(jsonPath("$.role").value("HOST"));

        User savedUser = userRepository.findByPhoneNumber("5145551234").orElseThrow();

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phoneNumber": "5145551234",
                                  "password": "Password1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.phoneNumber").value("5145551234"))
                .andExpect(jsonPath("$.role").value("HOST"));
    }
}
