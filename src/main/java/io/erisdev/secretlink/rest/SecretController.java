package io.erisdev.secretlink.rest;

import io.erisdev.secretlink.rest.dto.CreateSecretRequestDto;
import io.erisdev.secretlink.rest.dto.CreateSecretResponseDto;
import io.erisdev.secretlink.rest.dto.RevealSecretResponseDto;
import io.erisdev.secretlink.service.SecretService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/secrets")
@RequiredArgsConstructor
public class SecretController {

    private final SecretService secretService;



    @PostMapping("/create")
    public ResponseEntity<CreateSecretResponseDto> createSecret(@RequestBody CreateSecretRequestDto request) {
        return ResponseEntity.ok(secretService.createSecret(request));
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<RevealSecretResponseDto> revealSecret(@PathVariable Long id, @PathVariable String token) {
        return ResponseEntity.ok(secretService.revealSecret(id, token));
    }



}
