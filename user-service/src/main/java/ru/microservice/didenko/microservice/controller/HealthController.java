package ru.microservice.didenko.microservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.microservice.didenko.microservice.model.HealthResponse;
import ru.microservice.didenko.microservice.model.Status;

@RestController
public class HealthController {

    @GetMapping("/health")
    public HealthResponse getHealth() {
        return new HealthResponse(Status.OK);
    }
}
