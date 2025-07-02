package io.erisdev.secretlink.rest.dto;


import lombok.Data;

@Data
public class CreateSecretRequestDto {
    private String plainText;
    private long ttlHours;
}
