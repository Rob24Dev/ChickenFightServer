package dev.rwg.chickenfight.server.models.abilities;

import dev.rwg.chickenfight.server.models.Rarity;
import dev.rwg.chickenfight.server.models.cosmetics.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Table(name = "abilities")
public class Ability {

    @Id
    @UuidGenerator
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    private String displayName;
    private String description;
    @Enumerated(EnumType.STRING)
    private Role abilityRole;
    @Enumerated(EnumType.STRING)
    private Rarity rarity;
    private boolean isAvailableInContracts;
}
