package hu.unideb.autoszerviz.controller;

import hu.unideb.autoszerviz.model.CatalogService;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping("/catalog")
    public Map<String, Object> catalog() {
        return service.catalog();
    }
    }
