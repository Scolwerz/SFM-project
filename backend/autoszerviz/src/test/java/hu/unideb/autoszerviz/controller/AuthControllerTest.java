package hu.unideb.autoszerviz.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.model.AuthService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AuthControllerTest {
    AuthService service = mock(AuthService.class);
    AuthController controller = new AuthController(service);
    HttpSession session = mock(HttpSession.class);
    Map<String, String> body = Map.of(
        "name", "Test",
        "email", "t@e.st",
        "password", "password");

    @Test
    void registerDelegates() {
        when(service.register(body, session)).thenReturn(Map.of("id", 1));
        assertEquals(Map.of("id", 1), controller.register(body, session));
    }

    @Test
    void loginDelegates() {
        when(service.login(body, session)).thenReturn(Map.of("id", 1));
        assertEquals(Map.of("id", 1), controller.login(body, session));
    }

    @Test
    void logoutDelegates() {
        when(service.logout(session)).thenReturn(Map.of("message", "ok"));
        assertEquals(Map.of("message", "ok"), controller.logout(session));
    }

    @Test
    void meDelegates() {
        when(service.me(session)).thenReturn(Map.of("id", 1));
        assertEquals(Map.of("id", 1), controller.me(session));
    }

    @Test
    void profileDelegates() {
        when(service.profile(body, session)).thenReturn(Map.of("id", 1));
        assertEquals(Map.of("id", 1), controller.profile(body, session));
    }
}
