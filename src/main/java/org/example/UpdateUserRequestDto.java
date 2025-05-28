package org.example;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request DTO for updating an existing User.")
public class UpdateUserRequestDto {
    @Schema(description = "First name of the user", example = "Jane")
    private String firstName;
    @Schema(description = "Last name of the user", example = "Smith")
    private String lastName;
    @Schema(description = "Email address of the user", example = "jane.smith@example.com")
    private String email;
}