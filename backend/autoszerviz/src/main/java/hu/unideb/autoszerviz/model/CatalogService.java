package hu.unideb.autoszerviz.model;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.*;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    private final BrandRepository brands;
    private final VehicleModelRepository models;
    private final ServiceTypeRepository services;

    public CatalogService(BrandRepository brands, VehicleModelRepository models, ServiceTypeRepository services) {
        this.brands = brands;
        this.models = models;
        this.services = services;
    }

    public Map<String, Object> catalog() {
        return Map.of(
            "brands", brands.findByActiveTrueOrderByName().stream()
                .map(b -> Map.of(
                    "id", b.id,
                    "name", b.name,
                    "origin", b.originRegion == null ? "" : b.originRegion))
                .toList(),

            "models", models.findAll().stream()
                .filter(m -> m.active)
                .map(m -> Map.of(
                    "id", m.id,
                    "brandId", m.brand.id,
                    "name", m.name,
                    "fuel", m.fuelType == null ? "" : m.fuelType,
                    "engine", m.engineType == null ? "" : m.engineType))
                .toList(),

            "services", services.findByActiveTrueOrderByName().stream()
                .map(svc -> Map.of(
                    "id", svc.id,
                    "name", svc.name,
                    "description", svc.description == null ? "" : svc.description,
                    "durationMinutes", svc.durationMinutes))
                .toList());
    }
}
