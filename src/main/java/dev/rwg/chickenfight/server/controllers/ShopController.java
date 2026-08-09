package dev.rwg.chickenfight.server.controllers;

import dev.rwg.chickenfight.server.dto.CurrentOffersResponse;
import dev.rwg.chickenfight.server.services.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }


    @GetMapping("/offers")
    public ResponseEntity<?> getCurrentOffers() {
        return ResponseEntity.status(HttpStatus.OK).body(new CurrentOffersResponse(shopService.getCurrentShopOffers()));
    }
}
