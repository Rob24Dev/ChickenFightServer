package dev.rwg.chickenfight.server.services;

import dev.rwg.chickenfight.server.models.Rarity;
import dev.rwg.chickenfight.server.models.cosmetics.Cosmetic;
import dev.rwg.chickenfight.server.repositories.CosmeticRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CosmeticsService {

    private final CosmeticRepository cosmeticsRepository;

    public CosmeticsService(CosmeticRepository cosmeticsRepository) {
        this.cosmeticsRepository = cosmeticsRepository;
    }

    public Optional<Cosmetic> findById(UUID id) {
        return cosmeticsRepository.findById(id);
    }

    public List<Cosmetic> findCosmeticsByRarity(Rarity rarity) {
        return cosmeticsRepository.findCosmeticsByRarity(rarity);
    }

}
