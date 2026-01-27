package com.example.orderservice.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

//@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleUserNotFound(
//            ResourceNotFoundException ex) {
//
//        ErrorResponse error = new ErrorResponse(
//                LocalDateTime.now(),
//                ex.getMessage(),
//                "Resource not sufficient"
//        ) {
//            @Override
//            public HttpStatusCode getStatusCode() {
//                return null;
//            }
//
//            @Override
//            public ProblemDetail getBody() {
//                return null;
//            }
//        };
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//    }
}
