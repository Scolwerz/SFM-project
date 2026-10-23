package hu.unideb.autoszerviz.repository;

import hu.unideb.autoszerviz.entity.MechanicProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MechanicRepository extends JpaRepository<MechanicProfile, Long> {
}
