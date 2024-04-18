package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User and period pair must be unique!")
public class UserAndPaymentPairAlreadyExistsException extends RuntimeException {
}
