package dev.rwg.chickenfight.server.repositories;

import dev.rwg.chickenfight.server.models.shop.ShopOffer;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ShopOffersRepository extends JpaRepository<ShopOffer, Integer> {
    @Query("SELECT s FROM ShopOffer s WHERE s.endDate > :dateTime OR s.endDate IS NULL")
    List<ShopOffer> findAllByEndDateAfter(@Param("dateTime") LocalDateTime dateTime);

    @Transactional
    void deleteByEndDateBefore(@Nullable LocalDateTime dateTime);
}
