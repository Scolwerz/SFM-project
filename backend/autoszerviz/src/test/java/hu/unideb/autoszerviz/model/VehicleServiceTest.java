package hu.unideb.autoszerviz.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.*;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class VehicleServiceTest {
    AccessService access = mock(AccessService.class);
    BrandRepository brands = mock(BrandRepository.class);
    VehicleModelRepository models = mock(VehicleModelRepository.class);
    VehicleRepository vehicles = mock(VehicleRepository.class);
    VehicleService service = new VehicleService(access, brands, models, vehicles);
    HttpSession session = mock(HttpSession.class);
    User customer;
    Brand brand;
    VehicleModel model;

    @BeforeEach
    void configureCustomerAndCatalog() {
        customer = new User();
        customer.id = 1L;
        customer.role = Role.CUSTOMER;
        brand = new Brand();
        brand.id = 2L;
        brand.name = "Volvo";
        model = new VehicleModel();
        model.id = 3L;
        model.brand = brand;
        model.name = "V40";
        when(access.currentUser(session)).thenReturn(customer);
    }

    @Test
    void listsOwnedActiveVehicles() {
        when(vehicles.findByOwnerIdAndActiveTrueOrderByIdDesc(1L)).thenReturn(List.of());
        assertTrue(service.myVehicles(session).isEmpty());
    }

    @Test
    void mapsSavedVehicleDetails() {
        Vehicle vehicle = new Vehicle();
        vehicle.id = 7L;
        vehicle.brand = brand;
        vehicle.model = model;
        vehicle.year = 2021;
        vehicle.fuel = "DIESEL";
        vehicle.engine = "2.0";
        vehicle.licensePlate = "ABC-123";
        when(vehicles.findByOwnerIdAndActiveTrueOrderByIdDesc(1L)).thenReturn(List.of(vehicle));
        assertEquals("Volvo", service.myVehicles(session).get(0).get("brand"));
        assertEquals("ABC-123", service.myVehicles(session).get(0).get("licensePlate"));
    }

    @Test
    void savesVehicleWithSelectedBrandAndModel() {
        when(brands.findById(2L)).thenReturn(Optional.of(brand));
        when(models.findById(3L)).thenReturn(Optional.of(model));
        when(vehicles.save(any()))
            .thenAnswer(call -> {
                Vehicle v = call.getArgument(0);
                v.id = 8L;
                return v;
            });
        Map<String, Object> body = Map.of(
            "brandId", 2,
            "modelId", 3,
            "year", 2020,
            "fuel", "PETROL");
        assertEquals(8L, service.addVehicle(body, session).get("id"));
    }

    @Test
    void rejectsModelThatBelongsToDifferentBrand() {
        Brand other = new Brand();
        other.id = 10L;
        VehicleModel otherModel = new VehicleModel();
        otherModel.id = 3L;
        otherModel.brand = other;
        when(brands.findById(2L)).thenReturn(Optional.of(brand));
        when(models.findById(3L)).thenReturn(Optional.of(otherModel));
        Map<String, Object> body = Map.of(
            "brandId", 2,
            "modelId", 3,
            "year", 2020,
            "fuel", "PETROL");
        assertThrows(ResponseStatusException.class, () -> service.addVehicle(body, session));
    }

    @Test
    void removesVehicleBySoftDeactivation() {
        Vehicle vehicle = new Vehicle();
        vehicle.active = true;
        when(vehicles.findByIdAndOwnerIdAndActiveTrue(4L, 1L)).thenReturn(Optional.of(vehicle));
        service.deleteVehicle(4L, session);
        assertFalse(vehicle.active);
        verify(vehicles).save(vehicle);
    }
}
