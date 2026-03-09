package com.sergiocanzoneri.petapi;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiocanzoneri.petapi.controller.dto.PetFilterDto;
import com.sergiocanzoneri.petapi.controller.dto.PetInDto;
import com.sergiocanzoneri.petapi.controller.dto.PetUpdateDto;
import com.sergiocanzoneri.petapi.controller.impl.PetController;
import com.sergiocanzoneri.petapi.exception.GlobalExceptionHandler;
import com.sergiocanzoneri.petapi.exception.InvalidFilterException;
import com.sergiocanzoneri.petapi.exception.ResourceNotFoundException;
import com.sergiocanzoneri.petapi.model.Pet;
import com.sergiocanzoneri.petapi.service.IPetService;
import com.sergiocanzoneri.petapi.util.PetApiConstants;

@WebMvcTest(PetController.class)
@Import(GlobalExceptionHandler.class)
class PetApiMockMvcTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IPetService petService;

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /api/pets returns list of pets when no filters are provided")
    void getAllPets_returnsList() throws Exception {
        var snoopy = new Pet(1L, "Snoopy", "Dog", 3, "Sergio");
        var kitty = new Pet(2L, "Kitty", "Cat", 2, "Claudia");
        when(petService.findAll()).thenReturn(List.of(snoopy, kitty));

        mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Snoopy"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].species").value("Cat"));

        verify(petService).findAll();
        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("GET /api/pets/{id} returns pet when found")
    void getPetById_found() throws Exception {
        var snoopy = new Pet(10L, "Snoopy", "Dog", 3, "Sergio");
        when(petService.findById(10L)).thenReturn(snoopy);

        mockMvc.perform(get(PetApiConstants.PETS_ID_PATH, 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Snoopy"))
                .andExpect(jsonPath("$.species").value("Dog"));

        verify(petService).findById(10L);
        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("GET /api/pets/{id} returns 404 when pet is missing")
    void getPetById_notFound() throws Exception {
        when(petService.findById(999L)).thenThrow(new ResourceNotFoundException(PetApiConstants.PET_NOT_FOUND_PREFIX + "999"));

        mockMvc.perform(get(PetApiConstants.PETS_ID_PATH, 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$." + PetApiConstants.ERROR_KEY).value(PetApiConstants.PET_NOT_FOUND_PREFIX + "999"));

        verify(petService).findById(999L);
        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("POST /api/pets creates pet when request is valid")
    void createPet_validRequest() throws Exception {
        var inDto = new PetInDto("Snoopy", "Dog", 3, "Sergio");
        var created = new Pet(5L, "Snoopy", "Dog", 3, "Sergio");
        when(petService.create(any(PetInDto.class))).thenReturn(created);

        mockMvc.perform(post(PetApiConstants.PETS_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Snoopy"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.age").value(3))
                .andExpect(jsonPath("$.ownerName").value("Sergio"));

        verify(petService).create(any(PetInDto.class));
        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("POST /api/pets returns 400 when validation fails")
    void createPet_invalidRequest_returnsBadRequest() throws Exception {
        var invalid = new PetInDto("", "", -1, null);

        mockMvc.perform(post(PetApiConstants.PETS_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + PetApiConstants.ERROR_KEY).value(PetApiConstants.VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".name").exists())
                .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".species").exists())
                .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".age").exists());

        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("PUT /api/pets/{id} updates and returns pet when request is valid")
    void updatePet_validRequest() throws Exception {
        var updateDto = new PetUpdateDto("Snoopy Updated", "Dog", 4, "Claudia");
        var updated = new Pet(7L, "Snoopy Updated", "Dog", 4, "Claudia");
        when(petService.update(eq(7L), any(PetUpdateDto.class))).thenReturn(updated);

        mockMvc.perform(put(PetApiConstants.PETS_ID_PATH, 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.name").value("Snoopy Updated"))
                .andExpect(jsonPath("$.age").value(4))
                .andExpect(jsonPath("$.ownerName").value("Claudia"));

        verify(petService).update(eq(7L), any(PetUpdateDto.class));
        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("PUT /api/pets/{id} returns 400 when validation fails")
    void updatePet_invalidRequest_returnsBadRequest() throws Exception {
        var invalid = new PetUpdateDto("", "", -1, null);

        mockMvc.perform(put(PetApiConstants.PETS_ID_PATH, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$." + PetApiConstants.ERROR_KEY).value(PetApiConstants.VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".name").exists())
                .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".species").exists())
                .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".age").exists());

        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("DELETE /api/pets/{id} returns 204 when deletion succeeds")
    void deletePet_success() throws Exception {
        doNothing().when(petService).deleteById(11L);

        mockMvc.perform(delete(PetApiConstants.PETS_ID_PATH, 11L))
                .andExpect(status().isNoContent());

        verify(petService).deleteById(11L);
        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("DELETE /api/pets/{id} returns 404 when pet is missing")
    void deletePet_notFound() throws Exception {
        doThrow(new ResourceNotFoundException(PetApiConstants.PET_NOT_FOUND_PREFIX + "22"))
                .when(petService).deleteById(22L);

        mockMvc.perform(delete(PetApiConstants.PETS_ID_PATH, 22L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$." + PetApiConstants.ERROR_KEY).value(PetApiConstants.PET_NOT_FOUND_PREFIX + "22"));

        verify(petService).deleteById(22L);
        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("GET /api/pets?species=Dog returns pets filtered by species")
    void getPetsBySpecies() throws Exception {
        var snoopy = new Pet(1L, "Snoopy", "Dog", 3, "Sergio");
        var filter = new PetFilterDto("Dog", null, null, null, null, null);
        when(petService.findByFilters(filter)).thenReturn(List.of(snoopy));

        mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH).param("species", "Dog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].species").value("Dog"));

        verify(petService).findByFilters(filter);
        verifyNoMoreInteractions(petService);
    }

    @Test
    @DisplayName("GET /api/pets?ownerName=Sergio returns pets filtered by ownerName")
    void getPetsByOwnerName() throws Exception {
        var snoopy = new Pet(1L, "Snoopy", "Dog", 3, "Sergio");
        var filter = new PetFilterDto(null, "Sergio", null, null, null, null);
        when(petService.findByFilters(filter)).thenReturn(List.of(snoopy));

        mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH).param("ownerName", "Sergio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].ownerName").value("Sergio"));

        verify(petService).findByFilters(filter);
        verifyNoMoreInteractions(petService);
    }

    @Nested
    @DisplayName("Combined query parameter scenarios")
    @SuppressWarnings("unused")
    class CombinedQueryParameters {

        @Test
        @DisplayName("GET /api/pets with multiple filters applies all of them")
        void getPetsWithMultipleParams_appliesAllFilters() throws Exception {
            var snoopy = new Pet(1L, "Snoopy", "Dog", 3, "Sergio");
            var pluto = new Pet(2L, "Pluto", "Dog", 5, "Esmeralda");
            var filter = new PetFilterDto("Dog", "Sergio", "Snoopy", 3, null, null);
            when(petService.findByFilters(filter)).thenReturn(List.of(snoopy));

            mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH)
                            .param("species", "Dog")
                            .param("ownerName", "Sergio")
                            .param("name", "Snoopy")
                            .param("age", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].species").value("Dog"))
                    .andExpect(jsonPath("$[0].ownerName").value("Sergio"))
                    .andExpect(jsonPath("$[0].name").value("Snoopy"))
                    .andExpect(jsonPath("$[0].age").value(3));

            verify(petService).findByFilters(filter);
            verifyNoMoreInteractions(petService);
        }

        @Test
        @DisplayName("GET /api/pets with both age and minAge returns 400")
        void getPets_ageAndMinAge_returnsBadRequest() throws Exception {
            when(petService.findByFilters(argThat((PetFilterDto f) ->
                    f != null && f.hasConflictingAgeFilters())))
                    .thenThrow(new InvalidFilterException(PetApiConstants.INVALID_FILTER_AGE_MESSAGE));

            mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH)
                            .param("age", "3")
                            .param("minAge", "1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$." + PetApiConstants.ERROR_KEY).value(PetApiConstants.INVALID_FILTER_AGE_MESSAGE));
        }

        @Test
        @DisplayName("GET /api/pets with minAge and maxAge filters by age range")
        void getPets_minAgeMaxAge_filtersByRange() throws Exception {
            var young = new Pet(1L, "Snoopy", "Dog", 3, "Sergio");
            var old = new Pet(2L, "Rex", "Dog", 7, "Esmeralda");
            var filter = new PetFilterDto(null, null, null, null, 2, 5);
            when(petService.findByFilters(filter)).thenReturn(List.of(young));

            mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH)
                            .param("minAge", "2")
                            .param("maxAge", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].age").value(3));

            verify(petService).findByFilters(filter);
            verifyNoMoreInteractions(petService);
        }

        @Test
        @DisplayName("GET /api/pets?age=-1 returns 400 - age must be greater than or equal to 0")
        void getPets_negativeAge_returnsBadRequest() throws Exception {
            mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH).param("age", "-1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$." + PetApiConstants.ERROR_KEY).value(PetApiConstants.VALIDATION_FAILED_MESSAGE))
                    .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".age").exists());
            verifyNoMoreInteractions(petService);
        }

        @Test
        @DisplayName("GET /api/pets?minAge=-1 returns 400 - minAge must be greater than or equal to 0")
        void getPets_negativeMinAge_returnsBadRequest() throws Exception {
            mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH).param("minAge", "-1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$." + PetApiConstants.ERROR_KEY).value(PetApiConstants.VALIDATION_FAILED_MESSAGE))
                    .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".minAge").exists());
            verifyNoMoreInteractions(petService);
        }

        @Test
        @DisplayName("GET /api/pets?maxAge=-1 returns 400 - maxAge must be greater than or equal to 0")
        void getPets_negativeMaxAge_returnsBadRequest() throws Exception {
            mockMvc.perform(get(PetApiConstants.PETS_BASE_PATH).param("maxAge", "-1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$." + PetApiConstants.ERROR_KEY).value(PetApiConstants.VALIDATION_FAILED_MESSAGE))
                    .andExpect(jsonPath("$." + PetApiConstants.DETAILS_KEY + ".maxAge").exists());
            verifyNoMoreInteractions(petService);
        }
    }
}

