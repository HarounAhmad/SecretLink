package io.erisdev.secretlink.service;


import io.erisdev.secretlink.domain.Secret;
import io.erisdev.secretlink.rest.dto.CreateSecretRequestDto;
import io.erisdev.secretlink.rest.dto.CreateSecretResponseDto;
import io.erisdev.secretlink.rest.dto.RevealSecretResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SecretService {


    private final SecretRepository secretRepository;
    private final CryptoService cryptoService;

    public CreateSecretResponseDto createSecret(CreateSecretRequestDto requestDto) {

        CryptoResult cryptoResult = cryptoService.encrypt(requestDto.getPlainText());


        Secret secret = Secret.builder()
                .encryptedPayload(cryptoResult.getEncryptedPayload())
                .iv(cryptoResult.getIv())
                .accessToken(UUID.randomUUID().toString())
                .expiresAt(requestDto.getExpiresAt())
                .used(false)
                .build();


        secretRepository.save(secret);

        String accessUrl = String.format("/secret/%d/%s", secret.getId(), secret.getAccessToken());

        return CreateSecretResponseDto.builder()
                .accessUrl(accessUrl)
                .expiresAt(secret.getExpiresAt())
                .build();
    }


    public RevealSecretResponseDto revealSecret(Long id, String token) {
        Secret secret = secretRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Secret not found"));

        if (!secret.getAccessToken().equals(token)) {
            throw new RuntimeException("Invalid token");
        }

        if (secret.isUsed()) {
            throw new RuntimeException("Secret already used");
        }

        if (secret.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Secret expired");
        }

        String plaintext = cryptoService.decrypt(secret.getEncryptedPayload(), secret.getIv());
        secret.setUsed(true);
        secretRepository.save(secret);

        return RevealSecretResponseDto.builder()
                .plainText(plaintext)
                .expiresAt(secret.getExpiresAt())
                .build();
    }

}
