package org.example.orderservice.ExceptionHandler;


public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String message) {
        super(message);
    }
}
