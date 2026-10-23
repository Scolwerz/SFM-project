package hu.unideb.autoszerviz.entity;

import jakarta.persistence.*;

@Entity
@Table(name= "mechanic_profiles")
public class MechanicProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "max_parallel_jobs", nullable = false)
    private Integer maxParallelJobs = 1;

    public MechanicProfile(){

    }
    // Getter/Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getMaxParallelJobs() {
        return maxParallelJobs;
    }

    public void setMaxParallelJobs(Integer maxParallelJobs) {
        this.maxParallelJobs = maxParallelJobs;
    }
}
