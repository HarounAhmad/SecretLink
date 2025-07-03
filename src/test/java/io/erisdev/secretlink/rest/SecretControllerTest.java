package io.erisdev.secretlink.rest;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.erisdev.secretlink.rest.dto.CreateSecretRequestDto;
import io.erisdev.secretlink.rest.dto.CreateSecretResponseDto;
import io.erisdev.secretlink.rest.dto.RevealSecretResponseDto;
import io.erisdev.secretlink.service.SecretService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class SecretControllerTest {

    private final SecretService secretService = Mockito.mock(SecretService.class);

    private final SecretController secretController = new SecretController(secretService);

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(secretController).build();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Test
    void createSecret_ReturnsCreatedSecret() throws Exception {
        CreateSecretRequestDto requestDto = new CreateSecretRequestDto();
        requestDto.setPlainText("my secret");
        requestDto.setExpiresAt(java.time.Instant.now().plusSeconds(3600));

        CreateSecretResponseDto responseDto = CreateSecretResponseDto.builder()
                .accessUrl("/secret/1/token")
                .expiresAt(requestDto.getExpiresAt())
                .build();

        when(secretService.createSecret(any(CreateSecretRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/secrets/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessUrl").value("/secret/1/token"))
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    void revealSecret_ReturnsDecryptedSecret() throws Exception {
        RevealSecretResponseDto responseDto = RevealSecretResponseDto.builder()
                .plainText("my secret")
                .expiresAt(java.time.Instant.now().plusSeconds(3600))
                .build();

        when(secretService.revealSecret(1L, "token")).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/secrets/1/token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plainText").value("my secret"))
                .andExpect(jsonPath("$.expiresAt").exists());
    }

}