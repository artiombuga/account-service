package account.dto;

import account.model.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for {@link Payment}
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull
    @Pattern(regexp = ".+@acme.com", message = "Employee invalid format")
    private String employee;

    @NotNull(message = "Period cannot be null")
    @Pattern(regexp = "(0?[1-9]|1[0-2])-\\d+", message = "Period invalid format")
    private String period;

    @PositiveOrZero(message = "Salary cannot be negative!")
    private Long salary;
}