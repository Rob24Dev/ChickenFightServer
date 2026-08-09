package dev.rwg.chickenfight.server.models;


import java.util.Optional;

public enum Rarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHIC,
    HEAVEN;

    public Optional<Rarity> getNext() {
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal >= Rarity.values().length || Rarity.values()[nextOrdinal] == Rarity.HEAVEN) {
            return Optional.empty();
        }
        return Optional.of(Rarity.values()[nextOrdinal]);
    }
}
