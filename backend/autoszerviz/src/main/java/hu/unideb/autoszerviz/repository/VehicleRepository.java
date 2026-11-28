package hu.unideb.autoszerviz.repository;

import hu.unideb.autoszerviz.entity.Vehicle;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByOwnerIdAndActiveTrueOrderByIdDesc(Long ownerId);

    Optional<Vehicle> findByIdAndOwnerIdAndActiveTrue(Long id, Long ownerId);

    long countByOwnerIdAndActiveTrue(Long id);
}
