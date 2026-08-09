package dev.rwg.chickenfight.server.dto;

import dev.rwg.chickenfight.server.models.shop.ShopOffer;
import lombok.Data;

import java.util.List;

@Data
public class CurrentOffersResponse {

    private List<ShopOffer> currentOffers;

    public CurrentOffersResponse(List<ShopOffer> currentOffers) {
        this.currentOffers = currentOffers;
    }
}
