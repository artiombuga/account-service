package account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public record RoleDTO(
        @NotBlank String role,
        @NotBlank @Email String user,
        @NotBlank @Pattern(regexp = "GRANT|REMOVE") String operation) implements Serializable {
}