package dev.rwg.chickenfight.server.repositories;

import dev.rwg.chickenfight.server.models.Rarity;
import dev.rwg.chickenfight.server.models.cosmetics.Cosmetic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CosmeticRepository extends JpaRepository<Cosmetic, UUID> {

    List<Cosmetic> findCosmeticsByRarity(Rarity rarity);
}
