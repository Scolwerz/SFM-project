package hu.unideb.autoszerviz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.model.TicketService;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import org.junit.jupiter.api.Test;

class TicketControllerTest {
    TicketService service = mock(TicketService.class);
    TicketController controller = new TicketController(service);
    HttpSession session = mock(HttpSession.class);

    @Test
    void listReturnsFilteredTicketsFromTicketService() {
        Map<String, Object> item = Map.of("id", 1L);
        var expected = List.of(item);
        when(service.list("PENDING", session)).thenReturn(expected);
        assertEquals(expected, controller.list("PENDING", session));
    }

    @Test
    void detailReturnsSingleTicketDetailsFromTicketService() {
        Map<String, Object> expected = Map.of("id", 1L);
        when(service.detail(1L, session)).thenReturn(expected);
        assertEquals(expected, controller.detail(1L, session));
    }

    @Test
    void statusDelegatesStatusUpdateToTicketServiceAndReturnsUpdatedTicket() {
        Map<String, String> body = Map.of("status", "ACCEPTED");
        Map<String, Object> expected = Map.of("id", 1L, "status", "ACCEPTED");
        when(service.status(1L, body, session)).thenReturn(expected);
        assertEquals(expected, controller.status(1L, body, session));
    }

    @Test
    void confirmDelegatesCustomerConfirmationToTicketServiceAndReturnsUpdatedTicket() {
        Map<String, Boolean> body = Map.of("approved", true);
        Map<String, Object> expected = Map.of("id", 1L, "status", "RESOLVED");
        when(service.confirm(1L, body, session)).thenReturn(expected);
        assertEquals(expected, controller.confirm(1L, body, session));
    }
}