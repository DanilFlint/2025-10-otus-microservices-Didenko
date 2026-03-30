package ru.microservice.didenko.microservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.microservice.didenko.microservice.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
