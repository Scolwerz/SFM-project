package hu.unideb.autoszerviz.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    public User customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id")
    public Vehicle vehicle;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_type_id")
    public ServiceType serviceType;

    @ManyToOne
    @JoinColumn(name = "assigned_mechanic_id")
    public User mechanic;

    @Column(name = "start_at", nullable = false)
    public LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    public LocalDateTime endAt;

    @Column(length = 1000)
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TicketStatus status = TicketStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "closed_at")
    public LocalDateTime closedAt;

    @Version public long version;

    public Ticket() {}
}
