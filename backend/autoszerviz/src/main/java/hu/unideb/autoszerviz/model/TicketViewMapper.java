package hu.unideb.autoszerviz.model;

import hu.unideb.autoszerviz.entity.Ticket;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class TicketViewMapper {
    public Map<String, Object> toView(Ticket t) {
        Map<String, Object> x = new LinkedHashMap<>();
        
        x.put("id", t.id);
        
        x.put("customer", Map.of(
                    "id", t.customer.id,
                    "name", t.customer.fullName,
                    "email", t.customer.email));
        
        x.put("vehicle", Map.of(
                    "id", t.vehicle.id,
                    "brand", t.vehicle.brand.name,
                    "origin", t.vehicle.brand.originRegion == null ? "" : t.vehicle.brand.originRegion,
                    "model", t.vehicle.model.name,
                    "year", t.vehicle.year,
                    "fuel", t.vehicle.fuel == null ? "" : t.vehicle.fuel,
                    "engine", t.vehicle.engine == null ? "" : t.vehicle.engine,
                    "licensePlate", t.vehicle.licensePlate == null ? "" : t.vehicle.licensePlate));
        
        x.put("service", Map.of(
                    "id", t.serviceType.id,
                    "name", t.serviceType.name,
                    "durationMinutes", t.serviceType.durationMinutes));

        x.put("mechanic", t.mechanic == null ? null : Map.of(
                        "id", t.mechanic.id,
                        "name", t.mechanic.fullName));

        x.put("startAt", t.startAt.toString());

        x.put("endAt", t.endAt.toString());

        x.put("description", t.description == null ? "" : t.description);

        x.put("status", t.status.name());

        x.put("createdAt", t.createdAt.toString());

        return x;
    }


}
