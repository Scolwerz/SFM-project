package hu.unideb.autoszerviz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_brands")
public class VehicleBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "origin_region", length = 120)
    private String originRegion;

    @Column(nullable = false)
    private Boolean active = true;

    public VehicleBrand() {}

    //Getter / Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOriginRegion() { return originRegion; }
    public void setOriginRegion(String originRegion) { this.originRegion = originRegion; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}