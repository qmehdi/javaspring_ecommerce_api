package com.example.demo.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class SareetaExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LogManager.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Date date = new Date();
        String message = ex.getMessage();
        String description = request.getDescription(false);
        exceptionResponse.setDate(date);
        exceptionResponse.setMessage(message);
        exceptionResponse.setDescription(description);
        log.error("[Exception] [Exception Handler] Exception occurred at: " + request.getDescription(false));
        return new ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
