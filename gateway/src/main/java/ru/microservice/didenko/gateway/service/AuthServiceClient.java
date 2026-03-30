package ru.microservice.didenko.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class AuthServiceClient {
    @Value("${services.auth.base-url:http://auth-service}")
    private String authServiceBaseUrl;

    @Value("${services.auth.validate-path:/api/v1/auth/validate}")
    private String validatePath;

    public URI buildValidateUri(String token) {
        return UriComponentsBuilder
                .fromUriString(authServiceBaseUrl)
                .path(validatePath)
                .queryParam("token", token)
                .build()
                .toUri();
    }
}
