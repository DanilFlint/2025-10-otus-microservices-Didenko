package ru.microservice.didenko.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.microservice.didenko.authservice.model.LoginRequest;
import ru.microservice.didenko.authservice.model.User;
import ru.microservice.didenko.authservice.repository.UserRepository;
import ru.microservice.didenko.authservice.security.JwtTokenProvider;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
class AuthController {
    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;


    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@RequestBody User newUser) {
        return Mono.just(newUser).map(user -> {
                    user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                    return user;
                })
                .flatMap(userRepository::save)
                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser))
                .onErrorResume(DataIntegrityViolationException.class, e ->
                        Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "User with this login already exists"))
                )
                .onErrorResume(e -> {
                    if (e instanceof ResponseStatusException) {
                        return Mono.error(e);
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed"));
                });
    }

    @PostMapping("/token")
    public Mono<String> login(@RequestBody LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password())
                )
                .map(tokenProvider::generateToken)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found or disabled"
                )))
                .onErrorResume(BadCredentialsException.class, e ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid login or password"
                        ))
                )
                .onErrorResume(e -> {
                    if (e instanceof ResponseStatusException) return Mono.error(e);
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Authentication service error"
                    ));
                });
    }

    @GetMapping("/validate")
    public Mono<ResponseEntity<ValidateResponse>> validate(@RequestParam("token") String token) {
        return Mono.just(token)
                .filter(tokenProvider::validateToken)
                .map(validToken -> {
                    String userId = tokenProvider.getUserIdFromToken(validToken);
                    List<String> roles = tokenProvider.getRolesFromToken(validToken);
                    return ResponseEntity.ok(new ValidateResponse(userId, roles));
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid or expired token")));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new ErrorResponse(ex.getReason()));
    }

    record ValidateResponse(String userId, List<String> roles) {}
    record ErrorResponse(String message) {}
}
