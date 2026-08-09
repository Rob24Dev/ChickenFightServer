package dev.rwg.chickenfight.server.services;

import dev.rwg.chickenfight.server.dto.OpenCrateResponse;
import dev.rwg.chickenfight.server.exceptions.ActionNotAllowedException;
import dev.rwg.chickenfight.server.exceptions.ResourceNotFoundException;
import dev.rwg.chickenfight.server.models.*;
import dev.rwg.chickenfight.server.models.abilities.Ability;
import dev.rwg.chickenfight.server.models.cosmetics.Cosmetic;
import dev.rwg.chickenfight.server.models.crates.Crate;
import dev.rwg.chickenfight.server.models.crates.CrateDrop;
import dev.rwg.chickenfight.server.models.crates.RewardType;
import dev.rwg.chickenfight.server.repositories.CratesRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CratesService {

    private final CratesRepository cratesRepository;
    private final PlayersService playersService;
    private final AuditLogsService auditLogsService;
    private final Random random = new Random();
    private final AbilitiesService abilitiesService;
    private final CosmeticsService cosmeticsService;

    public CratesService(CratesRepository cratesRepository, PlayersService playersService, AuditLogsService auditLogsService, AbilitiesService abilitiesService, CosmeticsService cosmeticsService) {
        this.cratesRepository = cratesRepository;
        this.playersService = playersService;
        this.auditLogsService = auditLogsService;
        this.abilitiesService = abilitiesService;
        this.cosmeticsService = cosmeticsService;
    }

    public List<Crate> getCurrentCrates() {
        LocalDateTime now = LocalDateTime.now();
        cratesRepository.deleteByEndDateBefore(now);
        return cratesRepository.findAllByEndDateAfter(now);
    }
    @Transactional
    public OpenCrateResponse openCrate(UUID playerId, Integer crateId) {
        ChFightPlayer player = playersService.getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));

        Crate crate = cratesRepository.findById(crateId)
                .orElseThrow(() -> new ResourceNotFoundException("The crate was not found."));

        HashMap<String, Integer> pKeys = player.getPlayerKeys();
        if (!pKeys.containsKey(crate.getKeyName())) {
            throw new ActionNotAllowedException("The player doesn't have the key to this crate.");
        }
        pKeys.replace(crate.getKeyName(), pKeys.get(crate.getKeyName()) - 1);


        ChFightPlayer finalPlayer = player;
        PlayerCratePity playerCratePity = getCratePity(player, crate)
                .orElseGet(() -> {
                    PlayerCratePity newPity = new PlayerCratePity(finalPlayer, crate);
                    finalPlayer.getCratesPity().add(newPity);
                    return newPity;
                });
        CrateDrop drop = getWonDrop(crate, playerCratePity);
        HashMap<String, String> rewardData = drop.getRewardData();
        UUID reward = UUID.fromString(rewardData.get("reward"));
        int rewardAmount = 0;
        if(RewardType.valueOf(rewardData.get("type")) == RewardType.GEMS
                || RewardType.valueOf(rewardData.get("type")) == RewardType.COINS) rewardAmount = Integer.parseInt(rewardData.get("rewardAmount"));
        switch (RewardType.valueOf(rewardData.get("type"))) {
            case COINS -> player.setCoins(player.getCoins() + rewardAmount);
            case GEMS -> player.setGems(player.getGems() + rewardAmount);
            case KEY -> pKeys.replace(crate.getKeyName(), pKeys.get(crate.getKeyName()) + rewardAmount);
            case ABILITY -> {
                Ability ability = abilitiesService.findAbilityById(reward).orElseThrow(() -> new ResourceNotFoundException("An error occurred while claiming the reward."));
                player.addAbility(ability);
            }
            case COSMETIC -> {
                Cosmetic cosmetic = cosmeticsService.findById(reward).orElseThrow(() -> new ResourceNotFoundException("An error occurred while claiming the reward."));
                player.addCosmetic(cosmetic);
            }
        }
        player = playersService.savePlayer(player);

        auditLogsService.log(
                LogType.CRATE_OPEN,
                player,
                UUID.randomUUID().toString(),
                Map.of(
                        "crate_id", crateId,
                        "data", rewardData
                )
        );

        return new OpenCrateResponse(drop, player);
    }

    private CrateDrop getWonDrop(Crate crate, PlayerCratePity playerCratePity) {
        Rarity guaranteedRarity = getGuaranteedRarity(crate, playerCratePity);

        CrateDrop drop;
        if (guaranteedRarity != null) {
            drop = getWeightedDropByRarity(crate, guaranteedRarity);
        } else {
            drop = rollWeightedDrop(crate.getDrops());
        }

        updatePityCounters(playerCratePity, drop.getRarity(abilitiesService, cosmeticsService));

        return drop;
    }

    private static @Nullable Rarity getGuaranteedRarity(Crate crate, PlayerCratePity playerCratePity) {
        Rarity guaranteedRarity = null;

        if (crate.getPityMythic() != 0 && playerCratePity.getRollsSinceMythic() >= crate.getPityMythic()) {
            guaranteedRarity = Rarity.MYTHIC;
        } else if (crate.getPityLegendary() != 0 && playerCratePity.getRollsSinceLegendary() >= crate.getPityLegendary()) {
            guaranteedRarity = Rarity.LEGENDARY;
        } else if (crate.getPityEpic() != 0 && playerCratePity.getRollsSinceEpic() >= crate.getPityEpic()) {
            guaranteedRarity = Rarity.EPIC;
        }
        return guaranteedRarity;
    }

    public CrateDrop rollWeightedDrop(List<CrateDrop> drops) {
        if (drops.isEmpty()) {
            throw new ResourceNotFoundException("The crate does not contain any drops.");
        }

        double totalWeight = drops.stream().mapToDouble(CrateDrop::getWeight).sum();
        double randomValue = random.nextDouble() * totalWeight;

        for (CrateDrop drop : drops) {
            randomValue -= drop.getWeight();
            if (randomValue <= 0) {
                return drop;
            }
        }
        return drops.getFirst();
    }

    public CrateDrop getWeightedDropByRarity(Crate crate, Rarity rarity) {
        List<CrateDrop> rarityDrops = crate.getDrops().stream()
                .filter(drop -> drop.getRarity(abilitiesService, cosmeticsService) == rarity)
                .toList();

        if (rarityDrops.isEmpty()) {
            return rollWeightedDrop(crate.getDrops());
        }

        return rollWeightedDrop(rarityDrops);
    }

    private void updatePityCounters(PlayerCratePity pity, Rarity rarity) {
        switch (rarity) {
            case COMMON, RARE:
                pity.setRollsSinceMythic(pity.getRollsSinceMythic() + 1);
                pity.setRollsSinceLegendary(pity.getRollsSinceLegendary() + 1);
                pity.setRollsSinceEpic(pity.getRollsSinceEpic() + 1);
                break;
            case EPIC:
                pity.setRollsSinceMythic(pity.getRollsSinceMythic() + 1);
                pity.setRollsSinceLegendary(pity.getRollsSinceLegendary() + 1);
                pity.setRollsSinceEpic(0);
                break;
            case LEGENDARY:
                pity.setRollsSinceMythic(pity.getRollsSinceMythic() + 1);
                pity.setRollsSinceLegendary(0);
                pity.setRollsSinceEpic(0);
                break;
            case MYTHIC:
                pity.setRollsSinceMythic(0);
                pity.setRollsSinceLegendary(0);
                pity.setRollsSinceEpic(0);
                break;
        }
    }

    private Optional<PlayerCratePity> getCratePity(ChFightPlayer player, Crate crate) {
        return player.getCratesPity().stream()
                .filter(pity -> pity.getCrate().getId().equals(crate.getId()))
                .findFirst();
    }
}