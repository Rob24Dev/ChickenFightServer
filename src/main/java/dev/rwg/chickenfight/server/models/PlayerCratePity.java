package dev.rwg.chickenfight.server.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.rwg.chickenfight.server.models.crates.Crate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "players_crates_pity")

public class PlayerCratePity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "player_id", unique = true)
    ChFightPlayer holder;
    @ManyToOne
    @JoinColumn(name = "crate_id", unique = true)
    Crate crate;
    private int rollsSinceEpic;
    private int rollsSinceLegendary;
    private int rollsSinceMythic;

    public PlayerCratePity(ChFightPlayer holder, Crate crate) {
        this.holder = holder;
        this.crate = crate;
    }

}
