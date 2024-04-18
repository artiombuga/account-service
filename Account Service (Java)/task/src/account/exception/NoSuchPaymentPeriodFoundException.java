package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "There was no such payment for the given period!")
public class NoSuchPaymentPeriodFoundException extends RuntimeException {
    public NoSuchPaymentPeriodFoundException() {
    }
}
