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
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final AccessService access;

    public AuthService(UserRepository users, BCryptPasswordEncoder encoder, AccessService access) {
        this.users = users;
        this.encoder = encoder;
        this.access = access;
    }


    // Regisztrációs adatok ellenőrzése
    private void validateRegistrationData(String name, String email, String password) {
        validateName(name);
        validateEmail(email);
        validatePassword(password);
    }
    private void validateName(String name) {
        if (name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adj meg egy nevet.");
    }
    private void validateEmail(String email) {
        if (!email.matches(EMAIL_REGEX))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adj meg egy érvényes e-mailt.");
    }
    private void validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adj meg egy legalább 8 karakteres jelszót.");
    }
    private void checkEmailAvailability(String email) {
        boolean emailAlreadyExists = users.findByEmailIgnoreCase(email).isPresent();
        if (emailAlreadyExists)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Az e-mail cím foglalt.");
    }




    // Felhasználó adatok response
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
        String password = body.getOrDefault("password", "");
        
        validateRegistrationData(name, email, password);
        checkEmailAvailability(email);

        User user = new User();
        user.fullName = name;
        user.email = email;
        user.passwordHash = encoder.encode(password);
        user.role = Role.CUSTOMER;
        users.save(user);
        session.setAttribute("uid", user.id);
        return account(user);
    }

    public Map<String, Object> login(Map<String, String> body, HttpSession session) {
        String email = body.getOrDefault("email", "");
        String password = body.getOrDefault("password", "");
        User user = users.findByEmailIgnoreCase(email)
                .filter(account -> account.active)
                .filter(account -> encoder.matches(password, account.passwordHash))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hibás e-mail cím vagy jelszó."));

        session.setAttribute("uid", user.id);
        return account(user);
    }

    public Map<String, Object> me(HttpSession session) {
        User user = access.currentUser(session);
        return account(user);
    }
    
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "Sikeres kijelentkezés");
    }

    // Profiladatok módosítása
    public Map<String, Object> profile(Map<String, String> body, HttpSession session) {
        User user = access.currentUser(session);
        String name = body.getOrDefault("name", user.fullName).trim();
        String password = body.get("password");
        
        validateName(name);
        user.fullName = name;

        if (password != null && !password.isBlank()) {
            validatePassword(password);
            user.passwordHash = encoder.encode(password);
        }
    
        return account(users.save(user));
    }
}
