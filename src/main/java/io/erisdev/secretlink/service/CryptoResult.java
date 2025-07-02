package io.erisdev.secretlink.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CryptoResult {
    private String encryptedPayload;
    private String iv;
}
