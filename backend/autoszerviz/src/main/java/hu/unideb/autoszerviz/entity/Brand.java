package hu.unideb.autoszerviz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    public String name;

    @Column(name = "origin_region")
    public String originRegion;

    public boolean active = true;

    public Brand() {}
}
