package hu.unideb.autoszerviz.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceTest {
    UserRepository users = mock(UserRepository.class);
    BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    ModelAccessService access = mock(ModelAccessService.class);
    HttpSession session = mock(HttpSession.class);
    AuthService service = new AuthService(users, encoder, access);
    User customer;

    @BeforeEach
    void initCustomer() {
        customer = new User();
        customer.id = 9L;
        customer.fullName = "Kovács Anna";
        customer.email = "anna@example.com";
        customer.passwordHash = "hash";
        customer.role = Role.CUSTOMER;
    }

    @Test
    void registerSavesNormalizedCustomer() {
        Map<String, String> body = Map.of(
            "name", " Anna ",
            "email", " ANNA@example.com ",
            "password", "password1");
        when(users.findByEmailIgnoreCase("anna@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("password1")).thenReturn("encoded");
        when(users.save(any()))
            .thenAnswer(a -> {
                User value = a.getArgument(0);
                value.id = 9L;
                return value;
            });
        assertEquals("CUSTOMER", service.register(body, session).get("role"));
        verify(session).setAttribute("uid", 9L);
    }

    @Test
    void registerRejectsInvalidInput() {
        Map<String, String> body = Map.of(
            "name", "",
            "email", "bad",
            "password", "short");
        assertThrows(ResponseStatusException.class, () -> service.register(body, session));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        Map<String, String> body = Map.of(
            "name", "Anna",
            "email", "anna@example.com",
            "password", "password1");
        when(users.findByEmailIgnoreCase("anna@example.com")).thenReturn(Optional.of(customer));
        assertThrows(ResponseStatusException.class, () -> service.register(body, session));
    }

    @Test
    void loginStoresAuthenticatedCustomerInSession() {
        Map<String, String> body = Map.of(
            "email", customer.email,
            "password", "password1");
        when(users.findByEmailIgnoreCase(customer.email)).thenReturn(Optional.of(customer));
        when(encoder.matches("password1", "hash")).thenReturn(true);
        assertEquals("Kovács Anna", service.login(body, session).get("name"));
        verify(session).setAttribute("uid", 9L);
    }

    @Test
    void loginRejectsInvalidCredentials() {
        when(users.findByEmailIgnoreCase(customer.email)).thenReturn(Optional.of(customer));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> service.login(Map.of("email", customer.email, "password", "bad"), session));
    }

    @Test
    void logoutInvalidatesSession() {
        assertNotNull(service.logout(session).get("message"));
        verify(session).invalidate();
    }

    @Test
    void meReturnsTheCurrentAccount() {
        when(access.currentUser(session)).thenReturn(customer);
        assertEquals("anna@example.com", service.me(session).get("email"));
    }

    @Test
    void profileUpdatesNameAndPasswordHash() {
        when(access.currentUser(session)).thenReturn(customer);
        when(users.save(customer)).thenReturn(customer);
        when(encoder.encode("newpass1")).thenReturn("new-hash");
        Map<String, String> body = Map.of(
            "name", "Anna NemKovács",
            "password", "newpass1");
        assertEquals("Anna NemKovács", service.profile(body, session).get("name"));
        assertEquals("new-hash", customer.passwordHash);
    }

    @Test
    void profileRejectsBlankName() {
        when(access.currentUser(session)).thenReturn(customer);
        assertThrows(ResponseStatusException.class, () -> service.profile(Map.of("name", " "), session));
    }

    @Test
    void profileRejectsShortPassword() {
        when(access.currentUser(session)).thenReturn(customer);
        assertThrows(ResponseStatusException.class, () -> service.profile(Map.of("name", "Anna", "password", "short"), session));
    }
}
