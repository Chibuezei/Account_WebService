package account.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;


@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({NullPointerException.class})
    public void exceptionHandle(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
    @ExceptionHandler({ConstraintViolationException.class,RollbackException.class,MethodArgumentNotValidException.class})
    public void violationExceptionHandle(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void httpRequestMethodNotSupportedExceptionHandle(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }
    @ExceptionHandler(LockedException.class)
    public void LockedException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access locked!");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void httpMessageNotReadableException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }
}
