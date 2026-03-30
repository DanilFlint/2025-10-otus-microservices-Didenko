package ru.microservice.didenko.gateway.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long userId;
    private List<String> roles;
}
