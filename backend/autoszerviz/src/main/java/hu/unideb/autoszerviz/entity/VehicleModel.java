package hu.unideb.autoszerviz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_models")
public class VehicleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "brand_id")
    public Brand brand;

    @Column(nullable = false)
    public String name;

    @Column(name = "engine_type")
    public String engineType;

    @Column(name = "fuel_type")
    public String fuelType = "PETROL";

    public boolean active = true;

    public VehicleModel() {}
}
