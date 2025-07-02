package io.erisdev.secretlink.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RevealSecretResponseDto {
    private String plainText;
    private Instant expiresAt;
}
