package com.cheernota.riaratingreport.exception;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(
                String.format("Required parameter %s is missed",
                        ex.getBindingResult().getAllErrors()
                                .stream()
                                .map(error -> ((FieldError) error).getField())
                                .collect(Collectors.joining(", "))));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleNotReadableException(HttpMessageNotReadableException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ExceptionUtils.getRootCauseMessage(ex));
    }

    @ExceptionHandler(JsonParsingDBException.class)
    public ResponseEntity<String> handleJsonParsingDBException(JsonParsingDBException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(ExceptionUtils.getRootCauseMessage(ex));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ConnectionException.class)
    public ResponseEntity<String> handleConnectionException(ConnectionException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("Connection error, site can’t be reached");
    }

    @ExceptionHandler(WorkbookException.class)
    public ResponseEntity<String> handleWorkbookException(WorkbookException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("Internal error during writing the report, please shorten the sample or try again later");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error(ExceptionUtils.getRootCauseMessage(ex), ex);
        return ResponseEntity.internalServerError().body("Internal server error, please try again later");
    }
}
