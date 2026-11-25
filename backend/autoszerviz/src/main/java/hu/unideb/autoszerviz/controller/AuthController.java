package hu.unideb.autoszerviz.controller;

import hu.unideb.autoszerviz.model.AuthService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/auth/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body, HttpSession session) {
        return service.register(body, session);
    }

    @PostMapping("/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpSession session) {
        return service.login(body, session);
    }

    @PostMapping("/auth/logout")
    public Map<String, String> logout(HttpSession session) {
        return service.logout(session);
    }

    @GetMapping("/auth/me")
    public Map<String, Object> me(HttpSession session) {
        return service.me(session);
    }

    @PutMapping("/profile")
    public Map<String, Object> profile(@RequestBody Map<String, String> body, HttpSession session) {
        return service.profile(body, session);
    }


}
