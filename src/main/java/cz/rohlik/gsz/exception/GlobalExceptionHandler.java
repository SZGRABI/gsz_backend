package cz.rohlik.gsz.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleRequestBodyValidation(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ValidationError>> handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationError> errors = new ArrayList<>();

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String path = violation.getPropertyPath().toString();
            String field = path.contains(".")
                    ? path.substring(path.lastIndexOf(".") + 1)
                    : path;
            errors.add(new ValidationError(field, violation.getMessage()));
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RohlikException.class)
    public ResponseEntity<String> handleRohlikException(RohlikException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
