package org.pet.management.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.pet.management.components.frame.LoginFrame;
import org.pet.management.config.AppConfig;
import org.pet.management.config.RestEndpointConfiguration;
import org.pet.management.dto.request.LoginRequestDTO;
import org.pet.management.dto.request.OwnerUpdateDTO;
import org.pet.management.dto.request.PetUpdateDto;
import org.pet.management.dto.response.LoginResponseDTO;
import org.pet.management.dto.response.PetDetailsDTO;
import org.pet.management.dto.response.VaccineDTO;
import org.pet.management.exception.ApiCallException;
import org.pet.management.exception.TokenExpiredException;
import org.pet.management.listeners.SessionListener;

import javax.swing.*;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.pet.management.dto.response.LoginResponseDTO.loginFail;
import static org.pet.management.util.HttpStatusCode.INTERNAL_SERVER_ERROR;
import static org.pet.management.util.HttpStatusCode.UNAUTHORIZED;
import static org.pet.management.util.JsonUtil.asJsonString;

@Slf4j
@AllArgsConstructor
public class PetAppClient {
    private static final String PET_APP_V1 = "application/vnd.pet-app.v1+json";
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(20);
    private static final String AUTHORIZATION = "Authorization";
    private static final String SPACE_DELIMITER = " ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static String SEARCH_PET_URL;
    private static String UPDATE_PET_URL;
    private static String UPDATE_OWNER_URL;
    private static String VACCINE_RESOURCE_URL;
    private static PetAppClient petAppClient;
    private final RestEndpointConfiguration configuration;
    private final SessionListener sessionListener;

    private static final OkHttpClient okHttpClient =
            new OkHttpClient.Builder().connectTimeout(TIMEOUT_DURATION).readTimeout(TIMEOUT_DURATION).build();

    public static PetAppClient getClient() {
        if (petAppClient != null) {
            return petAppClient;
        }

        final var endpointConfiguration =
                RestEndpointConfiguration.builder().baseUrl(AppConfig.getProp("base.url"))
                        .loginUrl(AppConfig.getProp("login.url")).build();
        final SessionListener listener = () -> {
            JOptionPane.showMessageDialog(null, "Session expired. Please login again.");
            final var loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

        };
        petAppClient = new PetAppClient(endpointConfiguration, listener);
        SEARCH_PET_URL = format("%s/pets/search?name={name}", endpointConfiguration.getBaseUrl());
        UPDATE_PET_URL = format("%s/pets/{id}", endpointConfiguration.getBaseUrl());
        UPDATE_OWNER_URL = format("%s/owner/{id}", endpointConfiguration.getBaseUrl());
        VACCINE_RESOURCE_URL = format("%s/pet/{id}/vaccine", endpointConfiguration.getBaseUrl());
        return petAppClient;
    }

    public LoginResponseDTO login(final String username, final String password) {
        try {
            final var loginRequestDTO = LoginRequestDTO.from(username, password);
            final var postRequest = createPostRequest(
                    configuration.getLoginUrl(),
                    asJsonString(loginRequestDTO),
                    mapToHeaders(null, emptyMap())
            );
            final JsonNode jsonNode = newcall(postRequest, new TypeReference<>() {
            });
            final var token = jsonNode.get("token").asText();

            if (nonNull(token)) {
                configuration.setToken(token);
                return LoginResponseDTO.loginSuccess(token);
            } else {
                return loginFail("Some error occurred");
            }
        } catch (final Exception e) {
            log.error("Error while Login: {} error: {}", username, e.getMessage(), e);
            return loginFail("Invalid Username or Password");
        }
    }

    public List<PetDetailsDTO> getPetList() {
        final var endpoint = configuration.getBaseUrl() + "/pets";
        final var getRequest = createGetRequest(endpoint, mapToHeaders(configuration.getToken(), emptyMap()));
        return newcall(getRequest, new TypeReference<>() {
        });
    }

    public List<PetDetailsDTO> searchByName(final String name) {
        final var searchURL = SEARCH_PET_URL.replace("{name}", name);
        final var getRequest = createGetRequest(searchURL, mapToHeaders(configuration.getToken(), emptyMap()));
        return newcall(getRequest, new TypeReference<>() {
        });
    }

    public void updatePet(final Long id, final PetUpdateDto petUpdateDto) {
        final var jsonString = asJsonString(petUpdateDto);
        final var updatePetUrl = UPDATE_PET_URL.replace("{id}", String.valueOf(id));
        final var patchRequest = createPatchRequest(
                updatePetUrl,
                jsonString,
                mapToHeaders(configuration.getToken(), emptyMap())
        );
        newcall(patchRequest, new TypeReference<String>() {
        });
    }

    private Request createPatchRequest(final String endPoint, final String body, final Headers headers) {
        return new Request.Builder().url(endPoint).headers(headers).patch(RequestBody.create(body.getBytes(UTF_8)))
                .build();
    }

    private String post(
            final String token, final String endpoint, final String body, final Map<String, String> headers
    ) {
        final var postRequest = createPostRequest(endpoint, body, mapToHeaders(token, headers));
        return newcall(postRequest, new TypeReference<>() {
        });
    }

    public <T> T newcall(final Request request, final TypeReference<T> type) {
        try (final var response = okHttpClient.newCall(request).execute()) {
            log.debug("Got response from server: {}", response);

            if(response.isSuccessful()) {
                return parseResponse(response, type);
            }

            if (response.code() == UNAUTHORIZED) {
                handleUnauthorized();
                throw new TokenExpiredException("Unauthorized access! Please login again");
            } else if(response.code() == INTERNAL_SERVER_ERROR) {
                handleServerError(response);
                throw new TokenExpiredException(response.message());
            } else {
                throw new ApiCallException(response.code(), response.message());
            }

        } catch (final IOException e) {
            log.error("Error while making call to server {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleUnauthorized() throws TokenExpiredException {
        if(configuration.getToken() != null ) {
            JOptionPane.showMessageDialog(
                    null,
                    "Token Expired! Please relaunch app",
                    "Token Expired",
                    ERROR_MESSAGE
            );

            configuration.setToken(null);
        }
    }

    private static void handleServerError(final Response response) throws TokenExpiredException {
        JOptionPane.showMessageDialog(
                null,
                "Some error occurred! Please contact Admin",
                "Server Error",
                ERROR_MESSAGE
        );
    }

    private <T> T parseResponse(final Response response, final TypeReference<T> type) {
        try {
            final var body = response.body();
            if (type.getType() == Void.class) {
                return null;
            }

            if (type.getType() == String.class) {
                return (T) body.string();
            }

            final var responseBody = nonNull(body) ? body.string() : "{}";
            return JsonUtil.getObjectMapper().readValue(responseBody, type);
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

    private Request createPutRequest(final String endPoint, final String body, final Headers headers) {
        return new Request.Builder()
                .url(endPoint)
                .headers(headers)
                .put(RequestBody.create(body.getBytes(UTF_8)))
                .build();
    }

    private Headers mapToHeaders(final String token, final Map<String, String> headers) {
        final var builder = new Headers.Builder();
        Optional.ofNullable(token).map(t -> builder.set(AUTHORIZATION, getBearerToken(token)));

        builder.set(CONTENT_TYPE, PET_APP_V1);
        headers.forEach(builder::set);
        return builder.build();
    }

    private static String getBearerToken(final String token) {
        return java.lang.String.join(SPACE_DELIMITER, "Bearer", token);
    }

    public void updateOwner(final Long ownerId, final OwnerUpdateDTO ownerUpdateDTO) {
        final var jsonString = asJsonString(ownerUpdateDTO);
        final var updatePetUrl = UPDATE_OWNER_URL.replace("{id}", String.valueOf(ownerId));
        final var patchRequest =
                createPatchRequest(updatePetUrl, jsonString, mapToHeaders(configuration.getToken(), emptyMap()));
        newcall(patchRequest, new TypeReference<Void>() {
        });
    }

    public List<VaccineDTO> getVaccineByPet(final Long petId) {
        final var searchURL = VACCINE_RESOURCE_URL.replace("{id}", petId.toString());
        final var getRequest = createGetRequest(searchURL, mapToHeaders(configuration.getToken(), emptyMap()));
        return newcall(getRequest, new TypeReference<>() {
        });
    }

    public void updateVaccineData(final Long petId, final String jsonString) {
        final var searchURL = VACCINE_RESOURCE_URL.replace("{id}", petId.toString());
        final var getRequest = createPutRequest(searchURL, jsonString, mapToHeaders(
                configuration.getToken(),
                emptyMap()
        ));
        newcall(getRequest, new TypeReference<Void>() {});
    }
}
