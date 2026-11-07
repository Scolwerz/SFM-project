package hu.unideb.autoszerviz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private VehicleBrand brand;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private VehicleModel model;

    @Column(nullable = false)
    private Short year;

    @Column(name = "fuel_type", length = 50)
    private String fuelType;

    @Column(name = "engine_type", length = 100)
    private String engineType;

    @Column(name = "license_plate", length = 20)
    private String licensePlate;

    public Vehicle() {}

    //Getter / Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public VehicleBrand getBrand() { return brand; }
    public void setBrand(VehicleBrand brand) { this.brand = brand; }
    public VehicleModel getModel() { return model; }
    public void setModel(VehicleModel model) { this.model = model; }
    public Short getYear() { return year; }
    public void setYear(Short year) { this.year = year; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
}