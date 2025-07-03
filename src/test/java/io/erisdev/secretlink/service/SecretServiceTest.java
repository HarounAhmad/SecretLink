package io.erisdev.secretlink.service;

import io.erisdev.secretlink.domain.Secret;
import io.erisdev.secretlink.rest.dto.CreateSecretRequestDto;
import io.erisdev.secretlink.rest.dto.CreateSecretResponseDto;
import io.erisdev.secretlink.rest.dto.RevealSecretResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecretServiceTest {

    @Mock
    private SecretRepository secretRepository;

    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private SecretService secretService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSecret_ShouldEncryptAndSaveSecret_ReturnResponse() {
        CreateSecretRequestDto requestDto = new CreateSecretRequestDto();
        requestDto.setPlainText("my secret");
        Instant expiresAt = Instant.now().plusSeconds(3600);
        requestDto.setExpiresAt(expiresAt);

        CryptoResult cryptoResult = new CryptoResult("encryptedPayload", "iv");
        when(cryptoService.encrypt("my secret")).thenReturn(cryptoResult);

        Secret savedSecret = Secret.builder()
                .id(1L)
                .encryptedPayload(cryptoResult.getEncryptedPayload())
                .iv(cryptoResult.getIv())
                .accessToken("token-uuid")
                .expiresAt(expiresAt)
                .used(false)
                .build();

        when(secretRepository.save(any(Secret.class))).thenAnswer(invocation -> {
            Secret arg = invocation.getArgument(0);
            return Secret.builder()
                    .id(1L)
                    .encryptedPayload(arg.getEncryptedPayload())
                    .iv(arg.getIv())
                    .accessToken(arg.getAccessToken())
                    .expiresAt(arg.getExpiresAt())
                    .used(arg.isUsed())
                    .build();
        });

        CreateSecretResponseDto responseDto = secretService.createSecret(requestDto);

        assertNotNull(responseDto);
        System.out.println(responseDto.getAccessUrl());
        assertTrue(responseDto.getAccessUrl().contains("/secret/1/"));
        assertEquals(expiresAt, responseDto.getExpiresAt());

        verify(cryptoService).encrypt("my secret");
        verify(secretRepository).save(any(Secret.class));
    }

    @Test
    void revealSecret_ShouldReturnDecryptedSecret_WhenValid() {
        Long secretId = 1L;
        String token = UUID.randomUUID().toString();
        String encryptedPayload = "encryptedPayload";
        String iv = "iv";
        Instant expiresAt = Instant.now().plusSeconds(3600);

        Secret secret = Secret.builder()
                .id(secretId)
                .accessToken(token)
                .encryptedPayload(encryptedPayload)
                .iv(iv)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        when(secretRepository.findById(secretId)).thenReturn(Optional.of(secret));
        when(cryptoService.decrypt(encryptedPayload, iv)).thenReturn("my secret");

        RevealSecretResponseDto responseDto = secretService.revealSecret(secretId, token);

        assertNotNull(responseDto);
        assertEquals("my secret", responseDto.getPlainText());
        assertEquals(expiresAt, responseDto.getExpiresAt());

        assertTrue(secret.isUsed());
        verify(secretRepository).save(secret);
    }

    @Test
    void revealSecret_ShouldThrow_WhenSecretNotFound() {
        when(secretRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> secretService.revealSecret(1L, "token"));

        assertEquals("Secret not found", ex.getMessage());
    }

    @Test
    void revealSecret_ShouldThrow_WhenTokenInvalid() {
        Secret secret = Secret.builder()
                .id(1L)
                .accessToken("valid-token")
                .expiresAt(Instant.now().plusSeconds(3600))
                .used(false)
                .build();

        when(secretRepository.findById(1L)).thenReturn(Optional.of(secret));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> secretService.revealSecret(1L, "wrong-token"));

        assertEquals("Invalid token", ex.getMessage());
    }

    @Test
    void revealSecret_ShouldThrow_WhenSecretUsed() {
        Secret secret = Secret.builder()
                .id(1L)
                .accessToken("token")
                .expiresAt(Instant.now().plusSeconds(3600))
                .used(true)
                .build();

        when(secretRepository.findById(1L)).thenReturn(Optional.of(secret));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> secretService.revealSecret(1L, "token"));

        assertEquals("Secret already used", ex.getMessage());
    }

    @Test
    void revealSecret_ShouldThrow_WhenSecretExpired() {
        Secret secret = Secret.builder()
                .id(1L)
                .accessToken("token")
                .expiresAt(Instant.now().minusSeconds(10))
                .used(false)
                .build();

        when(secretRepository.findById(1L)).thenReturn(Optional.of(secret));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> secretService.revealSecret(1L, "token"));

        assertEquals("Secret expired", ex.getMessage());
    }
}