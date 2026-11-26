package hu.unideb.autoszerviz.model;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.*;
import jakarta.servlet.http.HttpSession;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final AccessService access;

    public AuthService(UserRepository users, BCryptPasswordEncoder encoder, AccessService access) {
        this.users = users;
        this.encoder = encoder;
        this.access = access;
    }

    private Map<String, Object> account(User user) {
        return Map.of(
            "id", user.id,
            "name", user.fullName,
            "email", user.email,
            "role", user.role.name());
    }

    public Map<String, Object> register(Map<String, String> body, HttpSession session) {
        String name = body.getOrDefault("name", "").trim();
        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String pass = body.getOrDefault("password", "");
        if (name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adj meg egy nevet.");
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adj meg egy érvényes e-mailt.");
        if (pass.length() < 8)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adj meg egy legalább 8 karakteres jelszót.");
        if (users.findByEmailIgnoreCase(email).isPresent())
            throw new ResponseStatusException( HttpStatus.CONFLICT, "Az e-mail cím már regisztrálva van.");

        User user = new User();
        user.fullName = name;
        user.email = email;
        user.passwordHash = encoder.encode(pass);
        user.role = Role.CUSTOMER;
        users.save(user);
        session.setAttribute("uid", user.id);
        return account(user);
    }

    public Map<String, Object> login(Map<String, String> body, HttpSession session) {
        String email = body.getOrDefault("email", "");
        String pass = body.getOrDefault("password", "");
        User user = users.findByEmailIgnoreCase(email)
            .filter(a -> a.active && encoder.matches(pass, a.passwordHash))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hibás e-mail cím vagy jelszó."));

        session.setAttribute("uid", user.id);
        return account(user);
    }

    public Map<String, Object> me(HttpSession session) {
        return account(access.currentUser(session));
    }
    
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "Sikeres kijelentkezés");
    }

    public Map<String, Object> profile(Map<String, String> body, HttpSession session) {
        User user = access.currentUser(session);
        String name = body.getOrDefault("name", user.fullName).trim();
        
        if (name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A név nem lehet üres.");
        user.fullName = name;

        if (body.containsKey("password") && !body.get("password").isBlank()) {
            if (body.get("password").length() < 8)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A jelszó legalább 8 karakter legyen.");
            user.passwordHash = encoder.encode(body.get("password"));
        }
        return account(users.save(user));
    }
}
