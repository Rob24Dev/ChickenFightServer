package dev.rwg.chickenfight.server.services;

import dev.rwg.chickenfight.server.models.Rarity;
import dev.rwg.chickenfight.server.models.abilities.Ability;
import dev.rwg.chickenfight.server.models.cosmetics.Cosmetic;
import dev.rwg.chickenfight.server.repositories.AbilitiesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class    AbilitiesService {

    private final AbilitiesRepository abilitiesRepository;

    public AbilitiesService(AbilitiesRepository abilitiesRepository) {
        this.abilitiesRepository = abilitiesRepository;
    }

    public Optional<Ability> findAbilityById(UUID id) {
        return abilitiesRepository.findById(id);
    }

    public List<Ability> findAbilitiesByRarity(Rarity rarity) {
        return abilitiesRepository.findAbilitiesByRarity(rarity);
    }

}
