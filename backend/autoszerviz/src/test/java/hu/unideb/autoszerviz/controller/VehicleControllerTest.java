package hu.unideb.autoszerviz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.model.VehicleService;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import org.junit.jupiter.api.Test;

class VehicleControllerTest {
    VehicleService service = mock(VehicleService.class);
    VehicleController controller = new VehicleController(service);
    HttpSession session = mock(HttpSession.class);

    @Test
    void listVehiclesDelegatesToModel() {
        List<Map<String, Object>> expected = List.of(Map.of("id", 1));
        when(service.myVehicles(session)).thenReturn(expected);
        assertEquals(expected, controller.myVehicles(session));
    }

    @Test
    void addVehicleDelegatesToModel() {
        Map<String, Object> body = Map.of("brandId", 1);
        Map<String, Object> expected = Map.of("id", 1);
        when(service.addVehicle(body, session)).thenReturn(expected);
        assertEquals(expected, controller.addVehicle(body, session));
    }

    @Test
    void removeVehicleDelegatesToModel() {
        controller.deleteVehicle(4L, session);
        verify(service).deleteVehicle(4L, session);
    }
}
