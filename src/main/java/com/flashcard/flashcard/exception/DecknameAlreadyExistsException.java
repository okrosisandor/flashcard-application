package com.flashcard.flashcard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DecknameAlreadyExistsException extends RuntimeException {

    public DecknameAlreadyExistsException(String message) {
        super(message);
    }
}
