package hu.unideb.autoszerviz.model;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.*;
import jakarta.servlet.http.HttpSession;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VehicleService {

    private final AccessService access;
    private final BrandRepository brands;
    private final VehicleModelRepository models;
    private final VehicleRepository vehicles;

    public VehicleService(AccessService access, BrandRepository brands, VehicleModelRepository models, VehicleRepository vehicles) {
        this.access = access;
        this.brands = brands;
        this.models = models;
        this.vehicles = vehicles;
    }

    public List<Map<String, Object>> myVehicles(HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);
        return vehicles.findByOwnerIdAndActiveTrueOrderByIdDesc(user.id).stream()
            .map(vehicle -> Map.<String, Object>of(
                "id", vehicle.id,
                "brandId", vehicle.brand.id,
                "brand", vehicle.brand.name,
                "origin", vehicle.brand.originRegion == null ? "" : vehicle.brand.originRegion,
                "modelId", vehicle.model.id,
                "model", vehicle.model.name,
                "year", vehicle.year,
                "fuel", vehicle.fuel == null ? "" : vehicle.fuel,
                "engine", vehicle.engine == null ? "" : vehicle.engine,
                "licensePlate", vehicle.licensePlate == null ? "" : vehicle.licensePlate))
            .toList();
    }

    public Map<String, Object> addVehicle(Map<String, Object> body, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);

        Vehicle vehicle = new Vehicle();
        vehicle.owner = user;
        vehicle.brand = brands.findById(Long.valueOf(body.get("brandId").toString())).orElseThrow();
        vehicle.model = models.findById(Long.valueOf(body.get("modelId").toString())).orElseThrow();
        if (!vehicle.model.brand.id.equals(vehicle.brand.id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A modell nem ehhez a márkához tartozik.");
        vehicle.year = Integer.parseInt(body.get("year").toString());
        vehicle.fuel = (String) body.get("fuel");
        vehicle.engine = (String) body.getOrDefault("engine", "");
        vehicle.licensePlate = (String) body.getOrDefault("licensePlate", "");
        vehicles.save(vehicle);
        return Map.of("id", vehicle.id);
    }

    public void deleteVehicle(Long id, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);
        Vehicle vehicle = vehicles
            .findByIdAndOwnerIdAndActiveTrue(id, user.id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        vehicle.active = false;
        vehicles.save(vehicle);
    }
}
