package hu.unideb.autoszerviz.model;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccessService {
    private final UserRepository users;

    public AccessService(UserRepository users) {
        this.users = users;
    }

    public User currentUser(HttpSession session) {
        Object id = session.getAttribute("uid");
        if (!(id instanceof Long userId))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return users
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    public User requireRole(User user, Role... allowed) {
        if (Arrays.stream(allowed).noneMatch(role -> role == user.role))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return user;
    }

}
