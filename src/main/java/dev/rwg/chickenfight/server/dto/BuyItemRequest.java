package dev.rwg.chickenfight.server.dto;

import dev.rwg.chickenfight.server.models.shop.CurrencyType;
import lombok.Data;

@Data
public class BuyItemRequest {

    private int offerId;
    private CurrencyType preferredCurrency;
    public BuyItemRequest(int offerId, CurrencyType preferredCurrency) {
        this.offerId = offerId;
        this.preferredCurrency = preferredCurrency;
    }
}
