package dev.rwg.chickenfight.server.controllers;

import dev.rwg.chickenfight.server.dto.AuthResponse;
import dev.rwg.chickenfight.server.dto.LoginRequest;
import dev.rwg.chickenfight.server.models.ChFightPlayer;
import dev.rwg.chickenfight.server.models.LogType;
import dev.rwg.chickenfight.server.models.Platform;
import dev.rwg.chickenfight.server.services.AuditLogsService;
import dev.rwg.chickenfight.server.services.AuthorizationService;
import dev.rwg.chickenfight.server.services.PlayersService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthorizationService authService;
    private final PlayersService playersService;
    private final AuditLogsService auditLogsService;

    public AuthController(AuthorizationService authService, PlayersService playersService, AuditLogsService auditLogsService) {
        this.authService = authService;
        this.playersService = playersService;
        this.auditLogsService = auditLogsService;
    }


    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        ChFightPlayer chFightPlayer;
        String platformId = loginRequest.getPlatformId();
        Platform platform = loginRequest.getPlatform();
        if(playersService.getPlayerByPlatformId(platformId).isPresent()) {
            chFightPlayer = playersService.getPlayerByPlatformId(platformId).get();
            auditLogsService.log(
                    LogType.LOGIN,
                    chFightPlayer,
                    UUID.randomUUID().toString(),
                    new HashMap<>()
            );
            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(authService.generateToken(chFightPlayer.getId()), chFightPlayer));
        }

        chFightPlayer = new ChFightPlayer();
        chFightPlayer.setPlatform(platform);
        chFightPlayer.setPlatformId(platformId);
        chFightPlayer.setName(playersService.getNewName());

        chFightPlayer = playersService.savePlayer(chFightPlayer);
        auditLogsService.log(
                LogType.REGISTER,
                chFightPlayer,
                UUID.randomUUID().toString(),
                new HashMap<>()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(authService.generateToken(chFightPlayer.getId()), chFightPlayer));
    }
}
