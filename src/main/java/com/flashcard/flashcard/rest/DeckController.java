package com.flashcard.flashcard.rest;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashcard.flashcard.entity.Deck;
import com.flashcard.flashcard.service.DeckService;
import com.flashcard.flashcard.service.ValidationService;

@RestController
@RequestMapping("/api/decks")
//To enable cross-domain communication (apply to all methods)
@CrossOrigin
public class DeckController {
	
	@Autowired
	private DeckService deckService;
	
	@Autowired
	private ValidationService validationService;
	
	@PostMapping("")
	public ResponseEntity<?> createDeck(@Valid @RequestBody Deck deck, BindingResult result, Principal principal){
		
		ResponseEntity<?> errors = validationService.validate(result);
		
		if(errors != null) {
			return errors;
		}
		
		Deck theDeck = deckService.createOrUpdateDeck(deck, principal.getName());
		
		return new ResponseEntity<Deck>(theDeck, HttpStatus.CREATED);
	}
	
	@GetMapping("")
	public Iterable<Deck> getAllDecks(Principal principal){
		
		return deckService.getDecks(principal.getName());
	}
	
	@GetMapping("/{deckId}")
	public ResponseEntity<?> getDeckById(@PathVariable Long deckId, Principal principal){
		
		Deck deck = deckService.findDeckById(deckId, principal.getName());
		
		return new ResponseEntity<Deck>(deck, HttpStatus.OK);
	}
	
	@DeleteMapping("/{deckId}")
	public ResponseEntity<?> deleteDeck(@PathVariable Long deckId, Principal principal){
		deckService.removeDeckById(deckId, principal.getName());
		
		return new ResponseEntity<String>("Deck with id '" + deckId + "' has successfully been deleted", HttpStatus.OK);
	}

}
