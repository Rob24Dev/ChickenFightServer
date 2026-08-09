package dev.rwg.chickenfight.server.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.rwg.chickenfight.server.exceptions.ResourceNotFoundException;
import dev.rwg.chickenfight.server.models.abilities.Ability;
import dev.rwg.chickenfight.server.models.abilities.AbilityPreset;
import dev.rwg.chickenfight.server.models.cosmetics.Cosmetic;
import dev.rwg.chickenfight.server.models.cosmetics.PlayerCosmetic;
import dev.rwg.chickenfight.server.models.shop.CurrencyType;
import dev.rwg.chickenfight.server.services.AbilitiesService;
import dev.rwg.chickenfight.server.services.CosmeticsService;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "players")
public class ChFightPlayer {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private Platform platform; // Android, Apple..

    @Column(unique = true)
    private String platformId;

    private int coins;
    private int gems;
    private int wins;
    private int losses;
    private int gamesPlayed;
    private int level;
    private int xp;

    @OneToMany(mappedBy = "holder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PlayerCratePity> cratesPity = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "player_abilities",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "ability_id")
    )
    private List<Ability> playerAbilities = new ArrayList<>();
    @OneToMany(mappedBy = "holder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PlayerCosmetic> playerCosmetics = new ArrayList<>();
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("player-presets")
    private List<AbilityPreset> presets = new ArrayList<>();
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String, Integer> playerKeys = new HashMap<>();

    public int getCurrency(CurrencyType currency) {
        return switch (currency) {
            case GEMS -> this.gems;
            case COINS -> this.coins;
        };
    }

    public void setCurrency(CurrencyType currency, int amount) {
        switch (currency) {
            case GEMS -> this.gems = amount;
            case COINS -> this.coins = amount;
        }
    }

    public void addItem(ItemType itemType, UUID itemId, CosmeticsService cosmeticsService, AbilitiesService abilitiesService) {
        switch (itemType) {
            case COSMETIC -> {
                Cosmetic cosmetic = cosmeticsService.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("The cosmetic with ID: " + itemId + " does not exist."));
                addCosmetic(cosmetic);
            }
            case ABILITY -> {
                Ability ability = abilitiesService.findAbilityById(itemId).orElseThrow(() -> new ResourceNotFoundException("The ability with ID: " + itemId + " does not exist."));
                addAbility(ability);
            }
        }
    }

    public void removeItem(ItemType itemType, UUID itemId, CosmeticsService cosmeticsService, AbilitiesService abilitiesService) {
        switch (itemType) {
            case COSMETIC -> {
                Cosmetic cosmetic = cosmeticsService.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("The cosmetic with ID: " + itemId + " does not exist."));
                removeCosmetic(cosmetic);
            }
            case ABILITY -> {
                Ability ability = abilitiesService.findAbilityById(itemId).orElseThrow(() -> new ResourceNotFoundException("The ability with ID: " + itemId + " does not exist."));
                removeAbility(ability);
            }
        }
    }


    public void addCosmetic(Cosmetic cosmetic) {
        PlayerCosmetic playerCosmetic = new PlayerCosmetic();
        playerCosmetic.setCosmetic(cosmetic);
        playerCosmetic.setHolder(this);
        playerCosmetics.add(playerCosmetic);
    }

    public void removeCosmetic(Cosmetic cosmetic) {
        if (getPlayerCosmetic(cosmetic) != null) playerCosmetics.remove(getPlayerCosmetic(cosmetic));
    }

    public void addAbility(Ability ability) {
        playerAbilities.add(ability);
    }

    public void removeAbility(Ability ability) {
        playerAbilities.remove(ability);
    }

    public PlayerCosmetic getPlayerCosmetic(Cosmetic cosmetic) {
        for (PlayerCosmetic playerCosmetic : this.playerCosmetics) {
            if (cosmetic.getId().equals(playerCosmetic.getCosmetic().getId())) return playerCosmetic;
        }
        return null;
    }
}