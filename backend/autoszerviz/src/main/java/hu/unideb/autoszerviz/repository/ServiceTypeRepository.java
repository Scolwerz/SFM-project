package hu.unideb.autoszerviz.repository;

import hu.unideb.autoszerviz.entity.ServiceType;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    List<ServiceType> findByActiveTrueOrderByName();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ServiceType s order by s.id")
    List<ServiceType> lockAllForBooking();
}
