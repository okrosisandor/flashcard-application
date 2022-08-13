package com.flashcard.flashcard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler
	public final ResponseEntity<Object> handleIdException(IdException ex, WebRequest request) {
		IdExceptionResponse exceptionResponse = new IdExceptionResponse(ex.getMessage());

		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler
	public final ResponseEntity<Object> handleNoDeckException(NoDeckException ex, WebRequest request) {

		//Since a 204 status code can't give back any response content, thereby it wasn't neccessary to create a NoDeckExceptionResponse class
		return new ResponseEntity<Object>("", HttpStatus.NO_CONTENT);
	}
	
	@ExceptionHandler
	public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request){
		NotFoundExceptionResponse exceptionResponse = new NotFoundExceptionResponse(ex.getMessage());
		
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
    public final ResponseEntity<Object> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, WebRequest request){
        UsernameAlreadyExistsResponse exceptionResponse = new UsernameAlreadyExistsResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler
    public final ResponseEntity<Object> handleDecknameAlreadyExists(DecknameAlreadyExistsException ex, WebRequest request){
        DecknameAlreadyExistsResponse exceptionResponse = new DecknameAlreadyExistsResponse(ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
