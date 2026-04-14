package com.soen345.ticketreserve.service;

import com.soen345.ticketreserve.dto.UserRegistrationRequest;
import com.soen345.ticketreserve.exception.BadRequestException;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.repository.UserRepository;
import com.soen345.ticketreserve.dto.LoginRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterUserWithEmail() {

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setEmail("ben@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("ben@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        User savedUser = new User();
        savedUser.setName("Benjamin");
        savedUser.setEmail("ben@test.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(request);

        assertEquals("Benjamin", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRegisterUserWithPhoneNumber() {

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setPhoneNumber("5141234567");
        request.setPassword("password123");

        when(userRepository.existsByPhoneNumber("5141234567")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        when(userRepository.save(any(User.class))).thenReturn(new User());

        assertNotNull(userService.registerUser(request));
    }

    @Test
    void shouldThrowErrorIfNoEmailOrPhone() {

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setPassword("password123");

        assertThrows(BadRequestException.class,
                () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowErrorIfEmailAlreadyExists() {

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setEmail("ben@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("ben@test.com")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowErrorIfPasswordTooShort() {

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setEmail("ben@test.com");
        request.setPassword("123");

        assertThrows(BadRequestException.class,
                () -> userService.registerUser(request));
    }

    @Test
    void shouldLoginUserWithEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ben@test.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setName("Benjamin");
        user.setEmail("ben@test.com");
        user.setPasswordHash("hashedPassword");
        user.setRole("CUSTOMER");

        when(userRepository.findByEmail("ben@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        User result = userService.loginUser(request);

        assertEquals("Benjamin", result.getName());
        assertEquals("ben@test.com", result.getEmail());
        verify(userRepository).findByEmail("ben@test.com");
        verify(passwordEncoder).matches("password123", "hashedPassword");
    }

    @Test
    void shouldLoginUserWithPhoneNumber() {
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("5141234567");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setName("Benjamin");
        user.setPhoneNumber("5141234567");
        user.setPasswordHash("hashedPassword");
        user.setRole("CUSTOMER");

        when(userRepository.findByPhoneNumber("5141234567")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        User result = userService.loginUser(request);

        assertEquals("5141234567", result.getPhoneNumber());
        verify(userRepository).findByPhoneNumber("5141234567");
        verify(passwordEncoder).matches("password123", "hashedPassword");
    }

    @Test
    void shouldThrowErrorIfLoginIdentifierMissing() {
        LoginRequest request = new LoginRequest();
        request.setPassword("password123");

        assertThrows(BadRequestException.class,
                () -> userService.loginUser(request));
    }

    @Test
    void shouldThrowErrorIfLoginPasswordMissing() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ben@test.com");

        assertThrows(BadRequestException.class,
                () -> userService.loginUser(request));
    }

    @Test
    void shouldThrowErrorIfLoginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ben@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("ben@test.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> userService.loginUser(request));
    }

    @Test
    void shouldThrowErrorIfLoginPasswordIncorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ben@test.com");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setEmail("ben@test.com");
        user.setPasswordHash("hashedPassword");

        when(userRepository.findByEmail("ben@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> userService.loginUser(request));
    }

    @Test
    void shouldThrowWhenUserIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> userService.getUserById(99L));
    }

    @Test
    void shouldRegisterEmailAsLowercase() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setEmail("Ben@Test.COM");
        request.setPassword("password123");

        when(userRepository.existsByEmail("ben@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerUser(request);

        assertEquals("ben@test.com", result.getEmail());
    }

    @Test
    void shouldLoginWithMixedCaseEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("Ben@Test.COM");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setName("Benjamin");
        user.setEmail("ben@test.com");
        user.setPasswordHash("hashedPassword");
        user.setRole("CUSTOMER");

        when(userRepository.findByEmail("ben@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        User result = userService.loginUser(request);

        assertEquals("ben@test.com", result.getEmail());
        verify(userRepository).findByEmail("ben@test.com");
    }

    @Test
    void shouldRegisterHostRoleWhenExplicitlyRequested() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setEmail("host@test.com");
        request.setPassword("password123");
        request.setRole("HOST");

        when(userRepository.existsByEmail("host@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerUser(request);

        assertEquals("HOST", result.getRole());
    }

    @Test
    void shouldDefaultRoleToCustomerWhenRoleIsNotHost() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setEmail("customer@test.com");
        request.setPassword("password123");
        request.setRole("ADMIN");

        when(userRepository.existsByEmail("customer@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerUser(request);

        assertEquals("CUSTOMER", result.getRole());
    }

    @Test
    void shouldThrowErrorIfPhoneAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Benjamin");
        request.setPhoneNumber("5141234567");
        request.setPassword("password123");

        when(userRepository.existsByPhoneNumber("5141234567")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowErrorIfNameIsBlank() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("   ");
        request.setEmail("ben@test.com");
        request.setPassword("password123");

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenIdIsInvalid() {
        assertThrows(BadRequestException.class, () -> userService.getUserById(-1L));
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        assertThrows(BadRequestException.class, () -> userService.getUserById(null));
    }

    @Test
    void shouldReturnUserWhenIdExists() {
        User user = new User();
        user.setId(5L);
        user.setName("Benjamin");

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(5L);

        assertEquals(5L, result.getId());
        assertEquals("Benjamin", result.getName());
    }
}
