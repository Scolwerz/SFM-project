package hu.unideb.autoszerviz.controller;

import hu.unideb.autoszerviz.model.BookingService;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping("/tickets/mine")
    public List<Map<String, Object>> mine(HttpSession session) {
        return service.mine(session);
    }

    @GetMapping("/availability")
    public List<String> availability(
        @RequestParam Long serviceId, @RequestParam String date, HttpSession session) {
        return service.availability(serviceId, date, session);
    }

    @PostMapping("/tickets")
    public Map<String, Object> create(@RequestBody Map<String, Object> body, HttpSession session) {
        return service.create(body, session);
    }

    @DeleteMapping("/tickets/{id}")
    public void cancel(@PathVariable Long id, HttpSession session) {
        service.cancel(id, session);
    }
}
