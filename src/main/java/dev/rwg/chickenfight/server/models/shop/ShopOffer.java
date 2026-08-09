package dev.rwg.chickenfight.server.models.shop;

import dev.rwg.chickenfight.server.models.ItemType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "shop_offers")
public class ShopOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private UUID itemId;
    private ItemType itemType;
    private int priceInCoins;
    private int priceInGems;
    @CreationTimestamp
    private LocalDateTime creationDate;
    private LocalDateTime endDate;

    public int getPriceInCurrency(CurrencyType currencyType) {
        switch (currencyType) {
            case COINS -> {
                return priceInCoins;
            }
            case GEMS -> {
                return  priceInGems;
            }
            default -> {
                return 0;
            }
        }
    }
}
