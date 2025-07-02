package io.erisdev.secretlink.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CreateSecretResponseDto {
    private String accessUrl;
    private Instant expiresAt;
}
