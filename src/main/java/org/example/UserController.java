package org.example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Статический импорт для удобства

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API for managing user resources") // Группировка в Swagger UI
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", responses = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@RequestBody CreateUserRequestDto createUserRequestDto) {
        UserResponseDto createdUserDto = userService.createUser(createUserRequestDto);
        return new ResponseEntity<>(addLinksToUser(createdUserDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a user by ID", responses = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> getUserById(
            @Parameter(description = "ID of the user to retrieve", example = "1")
            @PathVariable Long id) {
        try {
            UserResponseDto userDto = userService.getUserById(id);
            return ResponseEntity.ok(addLinksToUser(userDto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all users", responses = {
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CollectionModel.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDto>>> getAllUsers() {
        List<EntityModel<UserResponseDto>> users = userService.getAllUsers().stream()
                .map(this::addLinksToUser) // Добавляем ссылки к каждому пользователю
                .collect(Collectors.toList());

        // Добавляем ссылку на саму коллекцию
        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(users, selfLink));
    }

    @Operation(summary = "Update an existing user", responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> updateUser(
            @Parameter(description = "ID of the user to update", example = "1")
            @PathVariable Long id,
            @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        try {
            UserResponseDto updatedUserDto = userService.updateUser(id, updateUserRequestDto);
            return ResponseEntity.ok(addLinksToUser(updatedUserDto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a user", responses = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", example = "1")
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Вспомогательный метод для добавления HATEOAS ссылок к одному объекту UserResponseDto.
     * @param userDto DTO пользователя, к которому нужно добавить ссылки.
     * @return EntityModel с DTO пользователя и добавленными ссылками.
     */
    private EntityModel<UserResponseDto> addLinksToUser(UserResponseDto userDto) {
        // Ссылка на самого себя (получить этого пользователя)
        Link selfLink = linkTo(methodOn(UserController.class).getUserById(userDto.getId())).withSelfRel();
        // Ссылка на коллекцию всех пользователей
        Link allUsersLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("users");
        // Ссылка на обновление этого пользователя (с помощью PUT)
        Link updateUserLink = linkTo(methodOn(UserController.class).updateUser(userDto.getId(), null)).withRel("update");
        // Ссылка на удаление этого пользователя (с помощью DELETE)
        Link deleteUserLink = linkTo(methodOn(UserController.class).deleteUser(userDto.getId())).withRel("delete");

        // Создаем EntityModel и добавляем все ссылки
        return EntityModel.of(userDto, selfLink, allUsersLink, updateUserLink, deleteUserLink);
    }
}