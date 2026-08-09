package dev.rwg.chickenfight.server.models.abilities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.rwg.chickenfight.server.models.ChFightPlayer;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "player_ability_presets")
public class AbilityPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JsonBackReference("player-presets")
    @JoinColumn(name = "holder_id")
    private ChFightPlayer player;
    private boolean selected;
    @ManyToMany
    @JoinTable(
            name = "player_abilities",
            joinColumns = @JoinColumn(name = "preset_id"),
            inverseJoinColumns = @JoinColumn(name = "ability_id")
    )
    private List<Ability> abilities = new ArrayList<>();
}
