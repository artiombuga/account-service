package account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public record UserStateRequest(@NotNull String user,
                               @NotNull @Pattern(regexp = "LOCK|UNLOCK") String operation) implements Serializable {
}
