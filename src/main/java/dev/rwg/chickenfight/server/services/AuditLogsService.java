package dev.rwg.chickenfight.server.services;

import dev.rwg.chickenfight.server.models.AuditLog;
import dev.rwg.chickenfight.server.models.ChFightPlayer;
import dev.rwg.chickenfight.server.models.LogType;
import dev.rwg.chickenfight.server.repositories.AuditLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogsService {

    private final AuditLogsRepository auditLogsRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void log(LogType logType, ChFightPlayer player, String transactionId, Map<String, Object> metadataMap) {
        String jsonMetadata = null;

        if (metadataMap != null && !metadataMap.isEmpty()) {
            try {
                jsonMetadata = objectMapper.writeValueAsString(metadataMap);
            } catch (Exception e) {
                log.error("Failed to serialize audit log metadata for user: {}", player.getId(), e);
                jsonMetadata = "{\"error\": \"Serialization failed\"}";
            }
        }

        AuditLog auditLog = new AuditLog(logType, player, transactionId, jsonMetadata);
        auditLogsRepository.save(auditLog);
    }
}
