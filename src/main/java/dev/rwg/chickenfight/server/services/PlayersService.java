package dev.rwg.chickenfight.server.services;

import dev.rwg.chickenfight.server.dto.ContractResponse;
import dev.rwg.chickenfight.server.exceptions.ActionNotAllowedException;
import dev.rwg.chickenfight.server.exceptions.ResourceNotFoundException;
import dev.rwg.chickenfight.server.models.*;
import dev.rwg.chickenfight.server.models.abilities.Ability;
import dev.rwg.chickenfight.server.models.abilities.AbilityPreset;
import dev.rwg.chickenfight.server.models.cosmetics.Cosmetic;
import dev.rwg.chickenfight.server.models.cosmetics.PlayerCosmetic;
import dev.rwg.chickenfight.server.repositories.PlayersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayersService {

    private final PlayersRepository playersRepository;
    private final AuditLogsService auditLogsService;
    private final AbilitiesService abilitiesService;
    private final CosmeticsService cosmeticsService;

    public PlayersService(PlayersRepository playersRepository, AuditLogsService auditLogsService, AbilitiesService abilitiesService, CosmeticsService cosmeticsService) {
        this.playersRepository = playersRepository;
        this.auditLogsService = auditLogsService;
        this.abilitiesService = abilitiesService;
        this.cosmeticsService = cosmeticsService;
    }

    public ChFightPlayer savePlayer(ChFightPlayer player) {
        return playersRepository.save(player);
    }


    public Optional<ChFightPlayer> getPlayerById(UUID id) {
        return playersRepository.findById(id);
    }

    public Optional<ChFightPlayer> getPlayerByPlatformId(String id) {
        return playersRepository.findByPlatformId(id);
    }

    public String getNewName() {
        return "Guest-" + (playersRepository.count() + 1);
    }

    @Transactional
    public ChFightPlayer equipOrUnequipCosmetic(UUID playerId, int cosmeticIndex) {
        ChFightPlayer player = getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));
        List<PlayerCosmetic> playerCosmetics = player.getPlayerCosmetics();
        if(cosmeticIndex >= playerCosmetics.size() || cosmeticIndex < 0) throw new ResourceNotFoundException("The cosmetic was not found.");
        PlayerCosmetic playerCosmetic = playerCosmetics.get(cosmeticIndex);
        if(!playerCosmetic.isEquipped()) {
            playerCosmetics.stream().filter(pc1 -> pc1.getCosmetic().getCosmeticType() == playerCosmetic.getCosmetic().getCosmeticType()).forEach(PlayerCosmetic::unequip);
            playerCosmetic.equip();
        } else playerCosmetic.unequip();
        return playersRepository.save(player);
    }

    @Transactional
    public ChFightPlayer selectOrUnselectPreset(UUID playerId, int presetIndex) {
        ChFightPlayer player = getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));
        List<AbilityPreset> playerPresets = player.getPresets();
        if (presetIndex >= playerPresets.size() || presetIndex < 0)
            throw new ResourceNotFoundException("The preset was not found.");
        AbilityPreset preset = playerPresets.get(presetIndex);
        if (!preset.isSelected()) {
            playerPresets.stream().filter(AbilityPreset::isSelected).forEach(p -> p.setSelected(false));
            preset.setSelected(true);
        } else preset.setSelected(false);
        return playersRepository.save(player);
    }

    @Transactional
    public ContractResponse tradeUpContract(UUID playerId, List<ContractItem> contractItems) {
        if (contractItems == null || contractItems.size() != 10) {
            throw new ActionNotAllowedException("You must include exactly 10 items in the contract.");
        }

        ChFightPlayer player = getPlayerById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));

        Rarity sourceRarity = null;
        ItemType contractItemsType = null;
        for (ContractItem contractItem : contractItems) {
            switch (contractItem.getItemType()) {
                case ABILITY -> {
                    if (contractItemsType != null && contractItemsType != ItemType.ABILITY)
                        throw new ActionNotAllowedException("All inserted items must be of the same type.");
                    contractItemsType = ItemType.ABILITY;
                    Ability ability = abilitiesService.findAbilityById(contractItem.getItemUuid()).orElseThrow((() -> new ResourceNotFoundException("Abilitka s ID: " + contractItem.getItemUuid() + " neexistuje.")));
                    if (sourceRarity != null && sourceRarity != ability.getRarity())
                        throw new ActionNotAllowedException("All items added must have exactly the same rarity.");
                    sourceRarity = ability.getRarity();
                    if (!player.getPlayerAbilities().contains(ability))
                        throw new ResourceNotFoundException("Abilitku s ID " + contractItem.getItemUuid() + " nemáš v inventáři.");
                }
                case COSMETIC -> {
                    if (contractItemsType != null && contractItemsType != ItemType.COSMETIC)
                        throw new ActionNotAllowedException("All inserted items must be of the same type.");
                    contractItemsType = ItemType.COSMETIC;
                    Cosmetic cosmetic = cosmeticsService.findById(contractItem.getItemUuid()).orElseThrow((() -> new ResourceNotFoundException("Kosmetika s ID: " + contractItem.getItemUuid() + " neexistuje.")));
                    if (sourceRarity != null && sourceRarity != cosmetic.getRarity())
                        throw new ActionNotAllowedException("All items added must have exactly the same rarity.");
                    sourceRarity = cosmetic.getRarity();
                    if (player.getPlayerCosmetic(cosmetic) == null) {
                        throw new ResourceNotFoundException("You don't have the cosmetic item with ID ” + contractItem.getItemUuid() + “ in your inventory.");
                    }
                }
            }
        }
        if(sourceRarity == null) throw new ResourceNotFoundException("Something went wrong.");
        Rarity targetRarity = sourceRarity.getNext()
                .orElseThrow(() -> new ActionNotAllowedException("Mythic items can no longer be used to create a contract for a higher level."));

        List<UUID> rewardPool = new ArrayList<>();
        switch (contractItemsType) {
            case COSMETIC -> {
                List<Cosmetic> cosmetics = cosmeticsService.findCosmeticsByRarity(targetRarity).stream()
                        .filter(Cosmetic::isAvailableInContracts)
                        .toList();
                for(Cosmetic cosmetic : cosmetics) {
                    rewardPool.add(cosmetic.getId());
                }
            }
            case ABILITY -> {
                List<Ability> abilities = abilitiesService.findAbilitiesByRarity(targetRarity).stream()
                        .filter(Ability::isAvailableInContracts)
                        .toList();
                for(Ability ability : abilities) {
                    rewardPool.add(ability.getId());
                }
            }
        }

        if (rewardPool.isEmpty()) {
            throw new ResourceNotFoundException("No valid rewards were found in the database for the rarity " + targetRarity + ".");
        }

        UUID rewardItem = rewardPool.get(new Random().nextInt(rewardPool.size()));

        for (ContractItem contractItem : contractItems) player.removeItem(contractItem.getItemType(), contractItem.getItemUuid(), cosmeticsService, abilitiesService);

        player.addItem(contractItemsType, rewardItem, cosmeticsService, abilitiesService);
        player = playersRepository.save(player);

        auditLogsService.log(
                LogType.TRADE_CONTRACT,
                player,
                UUID.randomUUID().toString(),
                Map.of(
                        "entry_rarity", sourceRarity.name(),
                        "rewarded_item", rewardItem
                )
        );

        return new ContractResponse(rewardItem, player);
    }
}
