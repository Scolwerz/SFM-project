package hu.unideb.autoszerviz.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class ModelAccessServiceTest {
    UserRepository users = mock(UserRepository.class);
    ModelAccessService access = new ModelAccessService(users);
    HttpSession session = mock(HttpSession.class);

    @Test
    void currentUserResolvesSessionUser() {
        User user = new User();
        user.id = 3L;
        when(session.getAttribute("uid")).thenReturn(3L);
        when(users.findById(3L)).thenReturn(Optional.of(user));
        assertSame(user, access.currentUser(session));
    }

    @Test
    void currentUserRejectsMissingSessionUser() {
        when(session.getAttribute("uid")).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> access.currentUser(session));
    }

    @Test
    void requireRoleReturnsAllowedUser() {
        User user = new User();
        user.role = Role.CUSTOMER;
        assertSame(user, access.requireRole(user, Role.CUSTOMER));
    }

    @Test
    void requireRoleRejectsDisallowedUser() {
        User user = new User();
        user.role = Role.CUSTOMER;
        assertThrows(ResponseStatusException.class, () -> access.requireRole(user, Role.ADMIN));
    }
}
