package hu.unideb.autoszerviz.repository;

import hu.unideb.autoszerviz.entity.VehicleModel;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {
    List<VehicleModel> findByBrandIdAndActiveTrueOrderByName(Long brandId);
}
