package hu.unideb.autoszerviz.controller;

import hu.unideb.autoszerviz.model.TicketService;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TicketController {
    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping("/tickets")
    public List<Map<String, Object>> list(
        @RequestParam(required = false) String status, HttpSession session) {
        return service.list(status, session);
    }

    @GetMapping("/tickets/{id}")
    public Map<String, Object> detail(@PathVariable Long id, HttpSession session) {
        return service.detail(id, session);
    }

    @PutMapping("/tickets/{id}/status")
    public Map<String, Object> status(@PathVariable Long id, @RequestBody Map<String, String> body, HttpSession session) {
        return service.status(id, body, session);
    }

    @PutMapping("/tickets/{id}/confirm")
    public Map<String, Object> confirm(@PathVariable Long id, @RequestBody Map<String, Boolean> body, HttpSession session) {
        return service.confirm(id, body, session);
    }
}
