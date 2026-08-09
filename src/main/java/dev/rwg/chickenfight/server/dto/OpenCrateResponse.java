package dev.rwg.chickenfight.server.dto;

import dev.rwg.chickenfight.server.models.ChFightPlayer;
import dev.rwg.chickenfight.server.models.crates.CrateDrop;
import lombok.Data;

@Data
public class OpenCrateResponse {

    private CrateDrop wonDrop;
    private ChFightPlayer player;

    public OpenCrateResponse(CrateDrop wonDrop, ChFightPlayer player) {
        this.wonDrop = wonDrop;
        this.player = player;
    }
}
