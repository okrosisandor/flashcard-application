package com.flashcard.flashcard.exception;

public class IdExceptionResponse {

	private String message;

	public IdExceptionResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}
