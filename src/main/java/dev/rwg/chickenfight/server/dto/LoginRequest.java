package dev.rwg.chickenfight.server.dto;

import dev.rwg.chickenfight.server.models.Platform;
import lombok.Data;

@Data
public class LoginRequest {
    private Platform platform;
    private String platformId;

    public LoginRequest(Platform platform, String platformId) {
        this.platform = platform;
        this.platformId = platformId;
    }
}