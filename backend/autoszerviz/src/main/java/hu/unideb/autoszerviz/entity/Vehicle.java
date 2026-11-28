package hu.unideb.autoszerviz.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    public User owner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "brand_id")
    public Brand brand;

    @ManyToOne(optional = false)
    @JoinColumn(name = "model_id")
    public VehicleModel model;

    public int year;

    @Column(name = "license_plate")
    public String licensePlate;

    public String fuel;
    public String engine;
    public boolean active = true;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();

    public Vehicle() {}
}
