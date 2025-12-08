package ru.microservice.didenko.microservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.microservice.didenko.microservice.model.MicroserviceResponse;
import ru.microservice.didenko.microservice.model.Status;

@RestController
public class MicroserviceController {

    @GetMapping("/health/")
    public MicroserviceResponse getHealth() {
        return new MicroserviceResponse(Status.OK);
    }
}
