package dev.rwg.chickenfight.server.dto;

import dev.rwg.chickenfight.server.models.ChFightPlayer;

import java.util.UUID;

public class ContractResponse {

    public UUID resultEntityId;
    public ChFightPlayer player;

    public ContractResponse(UUID resultEntityId, ChFightPlayer player) {
        this.resultEntityId = resultEntityId;
        this.player = player;
    }
}
