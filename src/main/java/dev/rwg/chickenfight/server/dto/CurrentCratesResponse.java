package dev.rwg.chickenfight.server.dto;

import dev.rwg.chickenfight.server.models.crates.Crate;
import lombok.Data;

import java.util.List;

@Data
public class CurrentCratesResponse {

    private List<Crate> currentCrates;

    public CurrentCratesResponse(List<Crate> currentCrates) {
        this.currentCrates = currentCrates;
    }
}
