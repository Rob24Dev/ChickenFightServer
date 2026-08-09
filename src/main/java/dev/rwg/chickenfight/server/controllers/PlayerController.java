package dev.rwg.chickenfight.server.controllers;

import dev.rwg.chickenfight.server.dto.BuyItemRequest;
import dev.rwg.chickenfight.server.dto.ContractResponse;
import dev.rwg.chickenfight.server.exceptions.ResourceNotFoundException;
import dev.rwg.chickenfight.server.models.ChFightPlayer;
import dev.rwg.chickenfight.server.models.ContractItem;
import dev.rwg.chickenfight.server.services.CratesService;
import dev.rwg.chickenfight.server.services.PlayersService;
import dev.rwg.chickenfight.server.services.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayersService playersService;
    private final ShopService shopService;
    private final CratesService cratesService;

    public PlayerController(PlayersService playersService, ShopService shopService, CratesService cratesService) {
        this.playersService = playersService;
        this.shopService = shopService;
        this.cratesService = cratesService;
    }


    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestAttribute("authenticatedPlayerId") UUID playerId) {
        return playersService.getPlayerById(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/buyItem")
    public ResponseEntity<?> buyItem(@RequestAttribute("authenticatedPlayerId") UUID playerId, @RequestBody BuyItemRequest buyItemRequest) {
        ChFightPlayer updatedPlayer = shopService.processPurchase(playerId, buyItemRequest);
        return ResponseEntity.ok(updatedPlayer);
    }

    @PostMapping("/openCrate/{crateId}")
    public ResponseEntity<?> openCrate(@RequestAttribute("authenticatedPlayerId") UUID playerId, @PathVariable Integer crateId) {
        return ResponseEntity.ok(cratesService.openCrate(playerId, crateId));
    }

    @PostMapping("/contract")
    public ResponseEntity<?> contract(@RequestAttribute("authenticatedPlayerId") UUID playerId, @RequestBody List<ContractItem> contractItems) {
        ContractResponse contractResponse = playersService.tradeUpContract(playerId, contractItems);
        return ResponseEntity.ok(contractResponse);
    }

    @GetMapping("/abilities")
    public ResponseEntity<?> getAbilities(@RequestAttribute("authenticatedPlayerId") UUID playerId) {
        ChFightPlayer player = playersService.getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));
        return ResponseEntity.ok(player.getPlayerAbilities());
    }

    @GetMapping("/abilities/presets")
    public ResponseEntity<?> getAbilitiesPresets(@RequestAttribute("authenticatedPlayerId") UUID playerId) {
        ChFightPlayer player = playersService.getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));
        return ResponseEntity.ok(player.getPresets());
    }

    @GetMapping("/abilities/presets/{presetIndex}")
    public ResponseEntity<?> getAbilitiesPreset(@RequestAttribute("authenticatedPlayerId") UUID playerId, @PathVariable Integer presetIndex) {
        ChFightPlayer player = playersService.getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));
        if(presetIndex >= player.getPresets().size() || presetIndex < 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.ok(player.getPresets().get(presetIndex));
    }

    @PostMapping("/abilities/presets/{presetIndex}/select")
    public ResponseEntity<?> selectOrUnselectAbilitiesPreset(@RequestAttribute("authenticatedPlayerId") UUID playerId, @PathVariable Integer presetIndex) {
        return ResponseEntity.ok(playersService.selectOrUnselectPreset(playerId, presetIndex));
    }

    @GetMapping("/cosmetics")
    public ResponseEntity<?> getCosmetics(@RequestAttribute("authenticatedPlayerId") UUID playerId) {
        ChFightPlayer player = playersService.getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));
        return ResponseEntity.ok(player.getPlayerCosmetics());
    }

    @GetMapping("/cosmetics/{cosmeticIndex}")
    public ResponseEntity<?> getCosmetics(@RequestAttribute("authenticatedPlayerId") UUID playerId, @PathVariable Integer cosmeticIndex) {
        ChFightPlayer player = playersService.getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));
        return ResponseEntity.ok(player.getPlayerCosmetics().get(cosmeticIndex));
    }

    @GetMapping("/cosmetics/{cosmeticIndex}/equip")
    public ResponseEntity<?> selectCosmetic(@RequestAttribute("authenticatedPlayerId") UUID playerId, @PathVariable Integer cosmeticIndex) {
        return ResponseEntity.ok(playersService.equipOrUnequipCosmetic(playerId, cosmeticIndex));
    }
}
