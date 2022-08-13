package com.flashcard.flashcard.exception;

public class DecknameAlreadyExistsResponse {

    private String deckName;

	public DecknameAlreadyExistsResponse(String deckName) {
		this.deckName = deckName;
	}

	public String getDeckName() {
		return deckName;
	}

	public void setDeckName(String deckName) {
		this.deckName = deckName;
	}

    
}
