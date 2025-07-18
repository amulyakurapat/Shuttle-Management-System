package com.shuttle.SMS.service;
//
//public class UserAlreadyExistsException extends Throwable {
//    public UserAlreadyExistsException(String s) {
//    }
//}
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
