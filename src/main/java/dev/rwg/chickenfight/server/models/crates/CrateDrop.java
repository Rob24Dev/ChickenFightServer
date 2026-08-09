package dev.rwg.chickenfight.server.models.crates;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.rwg.chickenfight.server.exceptions.ResourceNotFoundException;
import dev.rwg.chickenfight.server.models.Rarity;
import dev.rwg.chickenfight.server.models.abilities.Ability;
import dev.rwg.chickenfight.server.models.cosmetics.Cosmetic;
import dev.rwg.chickenfight.server.services.AbilitiesService;
import dev.rwg.chickenfight.server.services.CosmeticsService;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.UUID;

@Entity
@Data
@Table(name = "crates_drops")
public class CrateDrop {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "crate_id")
    Crate crate;
    private double weight;
    @Column(columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String,String> rewardData;

    public Rarity getRarity(AbilitiesService abilitiesService, CosmeticsService cosmeticsService) {
        String error = "An error occurred while claiming the reward.";
        if(rewardData.get("rarity") == null || rewardData.get("reward") == null) throw new ResourceNotFoundException(error);

        switch (RewardType.valueOf(rewardData.get("type"))) {
            case COINS -> {
                if(rewardData.get("rarity") == null) return Rarity.COMMON;
                return Rarity.valueOf(rewardData.get("rarity"));
            }
            case GEMS -> {
                return Rarity.valueOf(rewardData.get("rarity"));
            }
            case ABILITY ->  {
                Ability ability = abilitiesService.findAbilityById(UUID.fromString(rewardData.get("reward"))).orElseThrow(() -> new ResourceNotFoundException(error));
                return ability.getRarity();
            }
            case COSMETIC -> {
                Cosmetic cosmetic = cosmeticsService.findById(UUID.fromString(rewardData.get("reward"))).orElseThrow(() -> new ResourceNotFoundException(error));
                return cosmetic.getRarity();
            }
        }
        throw new ResourceNotFoundException(error);
    }
}
