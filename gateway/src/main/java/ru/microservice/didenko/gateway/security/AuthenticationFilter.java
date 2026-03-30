package ru.microservice.didenko.gateway.security;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.microservice.didenko.gateway.dto.UserDTO;
import ru.microservice.didenko.gateway.service.AuthServiceClient;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient webClient;
    private final AuthServiceClient authServiceClient;

    public AuthenticationFilter(WebClient.Builder webClientBuilder, AuthServiceClient authServiceClient) {
        super(Config.class);
        this.webClient = webClientBuilder.build();
        this.authServiceClient = authServiceClient;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            return webClient.get()
                    .uri(authServiceClient.buildValidateUri(token))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed"))
                    )
                    .bodyToMono(UserDTO.class)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")))
                    .flatMap(userDto -> {
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", userDto.getUserId().toString())
                                .header("X-User-Roles", String.join(",", userDto.getRoles()))
                                .build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    .onErrorResume(e -> {
                        HttpStatus status = (e instanceof ResponseStatusException rs) ?
                                (HttpStatus) rs.getStatusCode() : HttpStatus.UNAUTHORIZED;
                        return onError(exchange, e.getMessage(), status);
                    });
        };
    }

    private Mono<Void> onError(org.springframework.web.server.ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    @Data
    public static class Config {
        private String requiredRole;
        public String getRequiredRole() { return requiredRole; }
        public void setRequiredRole(String requiredRole) { this.requiredRole = requiredRole; }
    }

}
