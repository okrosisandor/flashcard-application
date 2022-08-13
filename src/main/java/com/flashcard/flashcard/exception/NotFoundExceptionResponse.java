package com.flashcard.flashcard.exception;

public class NotFoundExceptionResponse {
	
	private String message;

	public NotFoundExceptionResponse(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
