package account.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDeleteResponse(
        @NotBlank String user,
        @NotBlank String status
) {

}
