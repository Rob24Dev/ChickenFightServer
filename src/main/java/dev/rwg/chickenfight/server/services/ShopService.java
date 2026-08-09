package dev.rwg.chickenfight.server.services;

import dev.rwg.chickenfight.server.dto.BuyItemRequest;
import dev.rwg.chickenfight.server.exceptions.ActionNotAllowedException;
import dev.rwg.chickenfight.server.exceptions.InsufficientFundsException;
import dev.rwg.chickenfight.server.exceptions.ResourceNotFoundException;
import dev.rwg.chickenfight.server.models.ChFightPlayer;
import dev.rwg.chickenfight.server.models.LogType;
import dev.rwg.chickenfight.server.models.shop.CurrencyType;
import dev.rwg.chickenfight.server.models.shop.ShopOffer;
import dev.rwg.chickenfight.server.repositories.ShopOffersRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ShopService {

    private final ShopOffersRepository shopOffersRepository;
    private final PlayersService playersService;
    private final AuditLogsService auditLogsService;
    @Getter
    private static ShopService instance;
    private final CosmeticsService cosmeticsService;
    private final AbilitiesService abilitiesService;

    public ShopService(ShopOffersRepository shopOffersRepository, PlayersService playersService, AuditLogsService auditLogsService, CosmeticsService cosmeticsService, AbilitiesService abilitiesService) {
        this.shopOffersRepository = shopOffersRepository;
        this.playersService = playersService;
        this.auditLogsService = auditLogsService;
        instance = this;
        this.cosmeticsService = cosmeticsService;
        this.abilitiesService = abilitiesService;
    }


    public List<ShopOffer> getCurrentShopOffers() {
        LocalDateTime now = LocalDateTime.now();
        shopOffersRepository.deleteByEndDateBefore(now);
        return shopOffersRepository.findAllByEndDateAfter(now);
    }

    @Transactional
    public ChFightPlayer processPurchase(UUID buyerId, BuyItemRequest buyItemRequest) {
        ChFightPlayer player = playersService.getPlayerById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("The player was not found."));

        ShopOffer shopOffer = shopOffersRepository.findById(buyItemRequest.getOfferId())
                .orElseThrow(() -> new ResourceNotFoundException("The offer was not found."));
        CurrencyType preferred = buyItemRequest.getPreferredCurrency();
        int balance = player.getCurrency(preferred);
        if((balance < shopOffer.getPriceInCurrency(preferred))) {
            throw new InsufficientFundsException("Currency Shortage " + buyItemRequest.getPreferredCurrency());
        }
        if(balance == 0) {
            throw new ActionNotAllowedException("This item cannot be purchased with currency " + preferred);
        }
        player.setCurrency(preferred, balance - shopOffer.getPriceInCurrency(preferred));
        player.addItem(shopOffer.getItemType(), shopOffer.getItemId(), cosmeticsService, abilitiesService);
        player = playersService.savePlayer(player);
        auditLogsService.log(
                LogType.ITEM_PURCHASE,
                player,
                UUID.randomUUID().toString(),
                Map.of(
                        "offer_id", buyItemRequest.getOfferId(),
                        "item_id", shopOffer.getItemId()));
        return player;
    }

}
