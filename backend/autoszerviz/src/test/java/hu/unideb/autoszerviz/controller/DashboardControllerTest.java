package hu.unideb.autoszerviz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.model.DashboardService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DashboardControllerTest {
    DashboardService service = mock(DashboardService.class);
    DashboardController controller = new DashboardController(service);
    HttpSession session = mock(HttpSession.class);

    @Test
    void dashboardReturnsRoleSpecificSummaryFromDashboardService() {
        Map<String, Object> expected = Map.of("active", 2L);
        when(service.dashboard(session)).thenReturn(expected);
        assertEquals(expected, controller.dashboard(session));
    }
}