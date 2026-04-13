package com.soen345.ticketreserve.integration;

import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.repository.EventRepository;
import com.soen345.ticketreserve.repository.ReservationRepository;
import com.soen345.ticketreserve.repository.UserRepository;
import com.soen345.ticketreserve.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected ReservationRepository reservationRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected EmailService emailService;

    @BeforeEach
    void cleanDatabase() {
        reservationRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected User createUser(String name, String email, String phoneNumber, String password, String role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    protected Event createEvent(User organizer, String title, LocalDate date, String location,
                                String category, int eventCapacity, String description, String status) {
        Event event = new Event();
        event.setOrganizer(organizer);
        event.setTitle(title);
        event.setEventDate(date);
        event.setLocation(location);
        event.setCategory(category);
        event.setEventCapacity(eventCapacity);
        event.setDescription(description);
        event.setStatus(status);
        return eventRepository.save(event);
    }
}
