package org.example;

import org.example.notification.dto.UserEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEventDTO> kafkaTemplate;

    @Autowired
    public UserService(UserRepository userRepository, KafkaTemplate<String, UserEventDTO> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public UserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
        User user = new User();
        user.setFirstName(createUserRequestDto.getFirstName());
        user.setLastName(createUserRequestDto.getLastName());
        user.setEmail(createUserRequestDto.getEmail());
        User savedUser = userRepository.save(user);
        sendUserCreatedEvent(savedUser.getEmail());
        return toUserResponseDto(savedUser);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + id + " не найден"));
        return toUserResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUser(Long id, UpdateUserRequestDto updateUserRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + id + " не найден"));

        if (updateUserRequestDto.getFirstName() != null) {
            existingUser.setFirstName(updateUserRequestDto.getFirstName());
        }
        if (updateUserRequestDto.getLastName() != null) {
            existingUser.setLastName(updateUserRequestDto.getLastName());
        }
        if (updateUserRequestDto.getEmail() != null) {
            existingUser.setEmail(updateUserRequestDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return toUserResponseDto(updatedUser);
    }

    public void deleteUser(Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + id + " не найден"));
        userRepository.deleteById(id);
        sendUserDeletedEvent(userToDelete.getEmail());
    }

    private UserResponseDto toUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    private void sendUserCreatedEvent(String email) {
        UserEventDTO event = new UserEventDTO("created", email);
        kafkaTemplate.send("user-events", event);
        LOGGER.info("Sent user created event to Kafka: {}", event);
    }

    private void sendUserDeletedEvent(String email) {
        UserEventDTO event = new UserEventDTO("deleted", email);
        kafkaTemplate.send("user-events", event);
        LOGGER.info("Sent user deleted event to Kafka: {}", event);
    }
}