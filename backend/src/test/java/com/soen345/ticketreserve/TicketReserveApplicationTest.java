package com.soen345.ticketreserve;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class TicketReserveApplicationTest {

    @Test
    void shouldDelegateMainToSpringApplicationRun() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            TicketReserveApplication.main(new String[]{"--spring.profiles.active=test"});

            springApplication.verify(() ->
                    SpringApplication.run(TicketReserveApplication.class, new String[]{"--spring.profiles.active=test"})
            );
        }
    }
}
