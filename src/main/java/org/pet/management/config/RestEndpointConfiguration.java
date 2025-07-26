package org.pet.management.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class RestEndpointConfiguration {
    final String baseUrl;
    final String loginUrl;
    @Setter
    String token;
}
