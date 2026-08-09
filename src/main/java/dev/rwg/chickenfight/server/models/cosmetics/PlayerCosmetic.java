package dev.rwg.chickenfight.server.models.cosmetics;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.rwg.chickenfight.server.models.ChFightPlayer;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "players_cosmetics")
public class PlayerCosmetic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "holder_id")
    ChFightPlayer holder;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Cosmetic cosmetic;
    private boolean equipped;


    public void equip() { this.equipped = true;}
    public void unequip() { this.equipped = false;}

}
