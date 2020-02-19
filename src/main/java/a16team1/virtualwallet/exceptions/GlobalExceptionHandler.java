package a16team1.virtualwallet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String CONNECTION_FAILED_WITH_PAYMENT_API = "Could not connect to external payment provider. Please try again later.";

    @ExceptionHandler(EntityNotFoundException.class)
    public void handleNonExistingDataRequest(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public void handleDuplicateDataRequest(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public void handleInsufficientFundsException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler({InvalidOperationException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public void handleInvalidOperationRequest(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({ResourceAccessException.class})
    public void handleOfflineBankApi(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.GATEWAY_TIMEOUT.value(), CONNECTION_FAILED_WITH_PAYMENT_API);
    }

}
