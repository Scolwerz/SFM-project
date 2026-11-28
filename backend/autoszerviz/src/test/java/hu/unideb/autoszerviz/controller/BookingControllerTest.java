package hu.unideb.autoszerviz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.model.BookingService;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import org.junit.jupiter.api.Test;

class BookingControllerTest {
    BookingService service = mock(BookingService.class);
    BookingController controller = new BookingController(service);
    HttpSession session = mock(HttpSession.class);

    @Test
    void mineReturnsCustomerBookingsFromBookingService() {
        Map<String, Object> item = Map.of("id", 1L);
        var expected = List.of(item);
        when(service.mine(session)).thenReturn(expected);
        assertEquals(expected, controller.mine(session));
    }

    @Test
    void availabilityReturnsAvailableTimeSlotsForGivenServiceAndDate() {
        List<String> expected = List.of("08:00", "08:30");
        when(service.availability(1L, "2026-12-01", session)).thenReturn(expected);
        assertEquals(expected, controller.availability(1L, "2026-12-01", session));
    }

    @Test
    void createDelegatesNewBookingRequestToBookingServiceAndReturnsResult() {
        Map<String, Object> body = Map.of("vehicleId", 1L, "serviceId", 2L);
        Map<String, Object> expected = Map.of("id", 10L);
        when(service.create(body, session)).thenReturn(expected);
        assertEquals(expected, controller.create(body, session));
    }

    @Test
    void cancelDelegatesTicketCancellationToBookingService() {
        controller.cancel(5L, session);
        verify(service).cancel(5L, session);
    }
}