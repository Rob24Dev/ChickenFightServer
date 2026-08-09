package dev.rwg.chickenfight.server.repositories;

import dev.rwg.chickenfight.server.models.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogsRepository extends JpaRepository<AuditLog, Long> {
}
