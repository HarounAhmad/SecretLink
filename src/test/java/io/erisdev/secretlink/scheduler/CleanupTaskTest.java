package io.erisdev.secretlink.scheduler;

import io.erisdev.secretlink.domain.Secret;
import io.erisdev.secretlink.service.SecretRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class CleanupTaskTest {
    @Mock
    private SecretRepository secretRepository;

    @Mock
    private Logger log;

    @InjectMocks
    private CleanupTask cleanupTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteExpiredOrUsedSecrets_DeletesCorrectSecrets() {
        Secret usedSecret = Secret.builder().used(true).expiresAt(Instant.now().plusSeconds(1000)).build();
        Secret expiredSecret = Secret.builder().used(false).expiresAt(Instant.now().minusSeconds(1000)).build();
        Secret validSecret = Secret.builder().used(false).expiresAt(Instant.now().plusSeconds(1000)).build();

        when(secretRepository.findAll()).thenReturn(List.of(usedSecret, expiredSecret, validSecret));

        cleanupTask.deleteExpiredOrUsedSecrets();

        // verify only used and expired secrets deleted
        verify(secretRepository).deleteAll(argThat(arg -> {
            if (!(arg instanceof List<?>)) return false;
            List<?> list = (List<?>) arg;
            return list.contains(usedSecret) && list.contains(expiredSecret) && !list.contains(validSecret);
        }));
    }

    @Test
    void deleteExpiredOrUsedSecrets_DoesNothingIfNoSecretsToDelete() {
        Secret validSecret = Secret.builder().used(false).expiresAt(Instant.now().plusSeconds(1000)).build();
        when(secretRepository.findAll()).thenReturn(List.of(validSecret));

        cleanupTask.deleteExpiredOrUsedSecrets();

        verify(secretRepository, never()).deleteAll(anyList());
    }

}