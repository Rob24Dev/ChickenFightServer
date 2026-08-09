package dev.rwg.chickenfight.server.repositories;

import dev.rwg.chickenfight.server.models.Rarity;
import dev.rwg.chickenfight.server.models.abilities.Ability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AbilitiesRepository extends JpaRepository<Ability, UUID> {

    List<Ability> findAbilitiesByRarity(Rarity rarity);
}
