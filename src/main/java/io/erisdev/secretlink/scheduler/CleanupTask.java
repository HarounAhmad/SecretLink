package io.erisdev.secretlink.scheduler;


import io.erisdev.secretlink.domain.Secret;
import io.erisdev.secretlink.service.SecretRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanupTask {

    private final SecretRepository secretRepository;

    @Scheduled(fixedRateString = "#{@schedulerConfig.rate}")
    public void deleteExpiredOrUsedSecrets() {
        Instant now = Instant.now();
        List<Secret> secretsToDelete = secretRepository.findAll().stream()
                .filter(secret -> secret.isUsed() || secret.getExpiresAt().isBefore(now))
                .toList();

        if (!secretsToDelete.isEmpty()) {
            secretRepository.deleteAll(secretsToDelete);
            log.info("Deleted {} expired or used secrets", secretsToDelete.size());
        }
    }
}
