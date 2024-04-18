package account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @JsonProperty("new_password")
        @NotBlank
        @Size(min = 12, message = "Password length must be 12 chars minimum!")
        String newPassword
) {

    @Override
    public String newPassword() {
        return newPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
               "newPassword='" + newPassword + '\'' +
               '}';
    }
}
