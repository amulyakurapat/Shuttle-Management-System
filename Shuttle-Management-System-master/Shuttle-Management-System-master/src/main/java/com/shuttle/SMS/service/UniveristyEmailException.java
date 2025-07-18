package com.shuttle.SMS.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//public class UniveristyEmailException extends Throwable {
//    public UniveristyEmailException(String s) {
//    }
//}



@ResponseStatus(HttpStatus.CONFLICT)
public class UniveristyEmailException extends RuntimeException {
    public UniveristyEmailException(String message) {
        super(message);
    }
}
