package ru.microservice.didenko.microservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.microservice.didenko.microservice.model.User;
import ru.microservice.didenko.microservice.repository.UserRepository;
import ru.microservice.didenko.microservice.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Получение всех пользователей
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    /**
     * Поиск пользователя по ID
     * @throws RuntimeException если пользователь не найден
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Обновление существующего пользователя
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        user.setName(userDetails.getName());
        user.setLastname(userDetails.getLastname());

        return userRepository.save(user);
    }

    /**
     * Удаление пользователя
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

}
