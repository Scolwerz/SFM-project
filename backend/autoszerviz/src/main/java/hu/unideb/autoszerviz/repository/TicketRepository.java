package hu.unideb.autoszerviz.repository;

import hu.unideb.autoszerviz.entity.*;
import jakarta.persistence.LockModeType;
import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCustomerIdOrderByStartAtDesc(Long id);

    List<Ticket> findAllByOrderByCreatedAtDesc();

    List<Ticket> findByStatusOrderByCreatedAtDesc(TicketStatus status);

    List<Ticket> findByMechanicIdOrderByStartAtDesc(Long id);

    long countByCustomerIdAndStatusIn(Long id, Collection<TicketStatus> s);

    @Query(
        "select count(t)>0 from Ticket t where t.mechanic.id=:mid and t.status in :statuses and"
            + " t.startAt<:end and t.endAt>:start")
    boolean mechanicOverlap(
        @Param("mid") Long mid,
        @Param("statuses") Collection<TicketStatus> statuses,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Ticket t where t.id=:id")
    Optional<Ticket> lockById(@Param("id") Long id);
}
