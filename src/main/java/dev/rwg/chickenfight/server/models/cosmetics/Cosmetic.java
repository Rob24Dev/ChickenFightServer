package dev.rwg.chickenfight.server.models.cosmetics;

import dev.rwg.chickenfight.server.models.Rarity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Table(name = "cosmetics")
public class Cosmetic {

    @Id
    @UuidGenerator
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    private String displayName;
    private String description;
    private CosmeticType cosmeticType;
    @Enumerated(EnumType.STRING)
    private Role cosmeticRole;
    @Enumerated(EnumType.STRING)
    private Rarity rarity;
    private boolean isAvailableInContracts;
}
