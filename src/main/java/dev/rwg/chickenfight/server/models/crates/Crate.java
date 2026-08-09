package dev.rwg.chickenfight.server.models.crates;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "crates")
public class Crate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String name;
    private String displayName;
    @Column(nullable = false, unique = true)
    private String keyName;
    @OneToMany(mappedBy = "crate")
    @JsonManagedReference
    private List<CrateDrop> drops;
    private int pityEpic;
    private int pityLegendary;
    private int pityMythic;
    @CreationTimestamp
    private LocalDateTime creationDate;
    private LocalDateTime endDate;
}
