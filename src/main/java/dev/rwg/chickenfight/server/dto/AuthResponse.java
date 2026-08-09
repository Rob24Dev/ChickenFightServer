package dev.rwg.chickenfight.server.dto;

import dev.rwg.chickenfight.server.models.ChFightPlayer;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private ChFightPlayer player;

    public AuthResponse(String token, ChFightPlayer player) {
        this.token = token;
        this.player = player;
    }
}