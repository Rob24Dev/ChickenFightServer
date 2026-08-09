package dev.rwg.chickenfight.server.repositories;

import dev.rwg.chickenfight.server.models.ChFightPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayersRepository extends JpaRepository<ChFightPlayer, UUID> {

    Optional<ChFightPlayer> findByPlatformId(String platformId);
}
