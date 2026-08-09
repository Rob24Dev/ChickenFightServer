package dev.rwg.chickenfight.server.repositories;

import dev.rwg.chickenfight.server.models.crates.Crate;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface CratesRepository extends JpaRepository<Crate, Integer> {
    @Query("SELECT s FROM Crate s WHERE s.endDate > :dateTime OR s.endDate IS NULL")
    List<Crate> findAllByEndDateAfter(@Param("dateTime") LocalDateTime dateTime);

    @Transactional
    void deleteByEndDateBefore(@Nullable LocalDateTime dateTime);
}
