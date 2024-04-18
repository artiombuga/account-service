package account.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO for {@link account.model.Payment}
 */
@Getter
@ToString
@EqualsAndHashCode
public class PaymentResponse implements Serializable {
    private final String name;
    private final String lastname;
    private final String period;
    private final String salary;

    public PaymentResponse(String name, String lastname, LocalDate period, Long salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = StringUtils.capitalize(period.getMonth().name().toLowerCase()) + "-" + period.getYear();
        this.salary = String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
    }
    
}