package org.pet.management.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.pet.management.config.AppConfig;
import org.pet.management.config.RestEndpointConfiguration;
import org.pet.management.dto.request.LoginRequestDTO;
import org.pet.management.dto.request.OwnerUpdateDTO;
import org.pet.management.dto.request.PetUpdateDto;
import org.pet.management.dto.response.LoginResponseDTO;
import org.pet.management.dto.response.PetDetailsDTO;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static org.pet.management.dto.response.LoginResponseDTO.loginFail;
import static org.pet.management.util.JsonUtil.asJsonString;
import static org.pet.management.util.JsonUtil.getObjectMapper;

@Slf4j
@AllArgsConstructor
public class PetAppClient {
    private static String PET_APP_V1 = "application/vnd.pet-app.v1+json";
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(20);
    private static final String AUTHORIZATION = "Authorization";
    private static final String SPACE_DELIMITER = " ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static String SEARCH_PET_URL;
    private static String UPDATE_PET_URL;
    private static String UPDATE_OWNER_URL;
    private static PetAppClient petAppClient;
    private final RestEndpointConfiguration configuration;

    private static final OkHttpClient okHttpClient =
            new OkHttpClient.Builder().connectTimeout(TIMEOUT_DURATION).readTimeout(TIMEOUT_DURATION).build();

    public static PetAppClient getClient() {
        if (petAppClient != null) {
            return petAppClient;
        }

        final RestEndpointConfiguration endpointConfiguration =
                RestEndpointConfiguration.builder().baseUrl(AppConfig.getProp("base.url"))
                        .loginUrl(AppConfig.getProp("login.url")).build();
        petAppClient = new PetAppClient(endpointConfiguration);
        SEARCH_PET_URL = format("%s/pets/search?name={name}", endpointConfiguration.getBaseUrl());
        UPDATE_PET_URL = format("%s/pets/{id}", endpointConfiguration.getBaseUrl());
        UPDATE_OWNER_URL = format("%s/owner/{id}", endpointConfiguration.getBaseUrl());
        return petAppClient;
    }

    public LoginResponseDTO login(final String username, final String password) {
        try {
            final LoginRequestDTO loginRequestDTO = LoginRequestDTO.from(username, password);
            final Request postRequest = createPostRequest(
                    configuration.getLoginUrl(),
                    asJsonString(loginRequestDTO),
                    mapToHeaders(null, emptyMap())
            );
            final JsonNode jsonNode = newcall(postRequest, JsonNode.class);
            final String token = jsonNode.get("token").asText();

            if (nonNull(token)) {
                configuration.setToken(token);
                return LoginResponseDTO.loginSuccess(token);
            } else {
                return loginFail("Some error occurred");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            log.error("Error while Login: {} error: {}", username, e.getMessage());
            return loginFail(e.getMessage());
        }
    }

    public List<PetDetailsDTO> getPetList() {
        final String endpoint = configuration.getBaseUrl() + "/pets";
        final Request getRequest = createGetRequest(endpoint, mapToHeaders(configuration.getToken(), emptyMap()));
        return getForListOfType(getRequest);
    }

    public List<PetDetailsDTO> searchByName(final String name) {
        final String searchURL = SEARCH_PET_URL.replace("{name}", name);
        final Request getRequest = createGetRequest(searchURL, mapToHeaders(configuration.getToken(), emptyMap()));
        return getForListOfType(getRequest);
    }

    public void updatePet(final int id, final PetUpdateDto petUpdateDto) {
        final String jsonString = asJsonString(petUpdateDto);
        final String updatePetUrl = UPDATE_PET_URL.replace("{id}", String.valueOf(id));
        final Request patchRequest =
                createPatchRequest(updatePetUrl, jsonString, mapToHeaders(configuration.getToken(), emptyMap()));
        newcall(patchRequest, String.class);
    }

    private Request createPatchRequest(final String endPoint, final String body, final Headers headers) {
        return new Request.Builder().url(endPoint).headers(headers)
                .patch(RequestBody.create(body.getBytes(UTF_8)))
                .build();
    }

    private String post(
            final String token, final String endpoint, final String body, final Map<String, String> headers
    ) {
        final Request postRequest = createPostRequest(endpoint, body, mapToHeaders(token, headers));
        return newcall(postRequest, String.class);
    }

    public <T> T newcall(final Request request, final Class<T> type) {
        log.debug("Sending request : {} to url: {}", request.body(), request.url());
        try (final var response = okHttpClient.newCall(request).execute()) {
            log.debug("Got response from server: {}", response);
            return parseResponse(response, type);
        } catch (final IOException e) {
            log.error("Error while making call to server {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<PetDetailsDTO> getForListOfType(final Request request) {

        try (final var response = PetAppClient.okHttpClient.newCall(request).execute()) {
            log.debug("Got response from server: {}", response);
            final ResponseBody body = response.body();
            return getObjectMapper().readValue(body.string(), new TypeReference<>() {
            });
        } catch (final IOException e) {
            log.error("exception while getting list {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private <T> T parseResponse(final Response response, final Class<T> type) {
        try {
            final ResponseBody body = response.body();
            if(type == String.class) {
                return (T) body.string();
            }
            final String responseBody = nonNull(body) ? body.string() : "{}";
            return getObjectMapper().readValue(responseBody, type);
        } catch (final IOException e) {
            log.error("Error while parsing response: {}", response);
            throw new RuntimeException(e);
        }
    }

    private Request createPostRequest(final String endPoint, final String body, final Headers headers) {
        return new Request.Builder().url(endPoint).headers(headers).post(RequestBody.create(body.getBytes(UTF_8)))
                .build();
    }

    private Request createGetRequest(final String endPoint, final Headers headers) {
        return new Request.Builder().url(endPoint).headers(headers).get().build();
    }

    private Headers mapToHeaders(final String token, final Map<String, String> headers) {
        final Headers.Builder builder = new Headers.Builder();
        Optional.ofNullable(token).map(t -> builder.set(AUTHORIZATION, getBearerToken(token)));

        builder.set(CONTENT_TYPE, PET_APP_V1);
        headers.forEach(builder::set);
        return builder.build();
    }

    private static String getBearerToken(final String token) {
        return java.lang.String.join(SPACE_DELIMITER, "Bearer", token);
    }

    public void updateOwner(final int ownerId, final OwnerUpdateDTO ownerUpdateDTO) {
        final String jsonString = asJsonString(ownerUpdateDTO);
        final String updatePetUrl = UPDATE_OWNER_URL.replace("{id}", String.valueOf(ownerId));
        final Request patchRequest =
                createPatchRequest(updatePetUrl, jsonString, mapToHeaders(configuration.getToken(), emptyMap()));
        newcall(patchRequest, String.class);
    }
}
