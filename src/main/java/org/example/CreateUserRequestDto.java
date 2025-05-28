package org.example;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request DTO for creating a new User.")
public class CreateUserRequestDto {
    @Schema(description = "First name of the user", example = "Jane", required = true)
    private String firstName;
    @Schema(description = "Last name of the user", example = "Smith", required = true)
    private String lastName;
    @Schema(description = "Email address of the user", example = "jane.smith@example.com", required = true)
    private String email;
}