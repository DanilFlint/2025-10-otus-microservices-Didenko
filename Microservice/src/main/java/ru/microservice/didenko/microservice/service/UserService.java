package ru.microservice.didenko.microservice.service;

import ru.microservice.didenko.microservice.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    User updateUser(Long id, User userDetails);

    void deleteUser(Long id);
}
