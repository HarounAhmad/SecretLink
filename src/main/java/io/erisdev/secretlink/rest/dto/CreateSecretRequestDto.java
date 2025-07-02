package io.erisdev.secretlink.rest.dto;


import lombok.Data;

import java.time.Instant;

@Data
public class CreateSecretRequestDto {
    private String plainText;
    private Instant expiresAt;
}
