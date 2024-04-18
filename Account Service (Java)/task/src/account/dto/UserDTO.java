package account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link account.model.User}
 */
public record UserDTO(@NotBlank Integer id,
                      @NotBlank String name,
                      @NotBlank String lastname,
                      @Pattern(regexp = ".*@acme\\.com") @NotBlank String email,
                      @NotBlank List<String> roles) implements Serializable {
}