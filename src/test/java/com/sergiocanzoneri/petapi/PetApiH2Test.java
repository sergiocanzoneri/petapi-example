package com.sergiocanzoneri.petapi;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiocanzoneri.petapi.controller.dto.PetInDto;
import com.sergiocanzoneri.petapi.controller.dto.PetOutDto;
import com.sergiocanzoneri.petapi.controller.dto.PetUpdateDto;
import com.sergiocanzoneri.petapi.util.PetApiConstants;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
class PetApiH2Test {

    @LocalServerPort
    int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String baseUrl() {
        return PetApiConstants.LOCALHOST_URL_PREFIX + port + PetApiConstants.PETS_BASE_PATH;
    }

    @Test
    void createAndRetrievePet() {
        PetInDto createBody = new PetInDto("Snoopy", "Dog", 3, "Sergio");

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl(),
                createBody,
                String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PetOutDto created = readPet(createResponse.getBody());
        assertThat(created.name()).isEqualTo("Snoopy");
        Long id = created.id();

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("Snoopy");
    }

    @Test
    void createPet_withInvalidRequest_returns400() {
        PetInDto invalidBody = new PetInDto("", "", -1, null);

        assertThatThrownBy(() -> restTemplate.postForEntity(
                        baseUrl(),
                        invalidBody,
                        String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> assertThat(((HttpClientErrorException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void updatePet() {
        PetInDto createBody = new PetInDto("Snoopy", "Dog", 3, "Sergio");

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl(),
                createBody,
                String.class);
        Long id = readPet(createResponse.getBody()).id();

        PetUpdateDto updateBody = new PetUpdateDto("Snoopy Updated", "Dog", 4, "Claudia");
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(updateBody),
                String.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).contains("Snoopy Updated");

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, String.class);
        assertThat(getResponse.getBody()).contains("Snoopy Updated");
    }

    @Test
    void deletePet() {
        PetInDto createBody = new PetInDto("Temp", "Cat", 1, null);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl(),
                createBody,
                String.class);
        Long id = readPet(createResponse.getBody()).id();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThatThrownBy(() -> restTemplate.getForEntity(baseUrl() + "/" + id, String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> assertThat(((HttpClientErrorException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getNonExistentPet_returns404() {
        assertThatThrownBy(() -> restTemplate.getForEntity(baseUrl() + "/99999", String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> assertThat(((HttpClientErrorException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getAllPets() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).startsWith("[");
    }

    private PetOutDto readPet(String json) {
        try {
            return objectMapper.readValue(json, PetOutDto.class);
        } catch (RuntimeException | IOException e) {
            throw new IllegalStateException(PetApiConstants.DESERIALIZE_PET_ERROR_MESSAGE, e);
        }
    }
}
