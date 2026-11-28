package hu.unideb.autoszerviz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_types")
public class ServiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    public String name;

    public String description;

    @Column(name = "duration_minutes", nullable = false)
    public int durationMinutes;

    public boolean active = true;

    public ServiceType() {}
}
