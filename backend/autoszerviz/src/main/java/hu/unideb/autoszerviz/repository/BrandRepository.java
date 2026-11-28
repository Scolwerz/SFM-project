package hu.unideb.autoszerviz.repository;

import hu.unideb.autoszerviz.entity.Brand;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByActiveTrueOrderByName();
}
