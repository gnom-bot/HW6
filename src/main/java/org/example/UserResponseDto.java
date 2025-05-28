package org.example;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Response DTO for User details, including HATEOAS links.")
public class UserResponseDto extends RepresentationModel<UserResponseDto> {
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;
    @Schema(description = "First name of the user", example = "John")

    private String firstName;
    @Schema(description = "Last name of the user", example = "Doe")

    private String lastName;
    @Schema(description = "Email address of the user", example = "john.doe@example.com")

    private String email;
}