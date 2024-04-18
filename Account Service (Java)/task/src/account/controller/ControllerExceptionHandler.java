package account.controller;

import account.exception.BreachedPasswordException;
import account.dto.CustomErrorResponse;
import account.exception.CannotLockAdministratorException;
import account.exception.CannotRemoveAdministratorException;
import account.exception.NoSuchPaymentPeriodFoundException;
import account.exception.RoleNotFoundException;
import account.exception.SamePasswordException;
import account.exception.UserAndPaymentPairAlreadyExistsException;
import account.exception.UserExistsException;
import account.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            UserExistsException.class,
            BreachedPasswordException.class,
            SamePasswordException.class,
            UserAndPaymentPairAlreadyExistsException.class,
            NoSuchPaymentPeriodFoundException.class,
            CannotRemoveAdministratorException.class,
            UserNotFoundException.class,
            RoleNotFoundException.class,
            CannotLockAdministratorException.class,
    })
    public ResponseEntity<CustomErrorResponse> handleUserValidationExceptions(Exception e, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getCause().getMessage(),
                request.getContextPath()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                LocalDateTime.now(),
                status.value(),
                HttpStatus.valueOf(status.value()).getReasonPhrase(),
                ex.getBindingResult().getFieldError().getDefaultMessage(),
                request.getDescription(false).substring(4)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; ")),
                request.getDescription(false).substring(4)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
