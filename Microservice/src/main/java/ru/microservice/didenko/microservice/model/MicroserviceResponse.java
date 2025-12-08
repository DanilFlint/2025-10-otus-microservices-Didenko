package ru.microservice.didenko.microservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MicroserviceResponse {
    private Status status;
}
