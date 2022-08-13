package com.flashcard.flashcard.rest;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.flashcard.flashcard.entity.Card;
import com.flashcard.flashcard.entity.Deck;
import com.flashcard.flashcard.entity.LearningCardsNumbers;
import com.flashcard.flashcard.service.CardService;
import com.flashcard.flashcard.service.DeckService;
import com.flashcard.flashcard.service.ValidationService;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CardController {

	@Autowired
	private ValidationService validationService;

	@Autowired
	private CardService cardService;
	
	@Autowired
	private DeckService deckService;

	@PostMapping("/{deckId}/cards")
	public ResponseEntity<?> createCard(@Valid @RequestBody Card card, BindingResult result, @PathVariable Long deckId, Principal principal) {

		ResponseEntity<?> errors = validationService.validate(result);

		if (errors != null) {
			return errors;
		}

		Card theCard = cardService.createCard(card, deckId, principal.getName());

		return new ResponseEntity<Card>(theCard, HttpStatus.CREATED);
	}
	
	
	@PostMapping("/{deckId}/upload")
	public ResponseEntity<?> uploadMultipleCardFromFile(@RequestParam("file") MultipartFile file, @PathVariable Long deckId, Principal principal) {
		String message = "";
		
		try {
		      List<Card> cards = cardService.saveMultipleCards(file, deckId, principal.getName());
		      
		      return new ResponseEntity<List<Card>>(cards, HttpStatus.OK);
		    } catch (Exception e) {
		      message = "File upload failed";
		      return new ResponseEntity<String>(message, HttpStatus.EXPECTATION_FAILED);
		    }
	}

	@GetMapping("/{deckId}/cards")
	public List<Card> retrieveAllCardsForDeck(@PathVariable Long deckId, Principal principal) {

		return cardService.retrieveCardsForDeck(deckId, principal.getName());
	}

	@GetMapping("/{deckId}/learning")
	public Iterable<Card> getCardsForLearning(@PathVariable Long deckId, Principal principal) {

		return cardService.retrieveCardsForLearning(deckId, principal.getName());
	}
	
	@GetMapping("/{deckId}/learning/numbers")
	public ResponseEntity<?> getCardNumbersForLearning(@PathVariable Long deckId, Principal principal) {
		
		Iterable<Card> cards = cardService.retrieveCardsForLearning(deckId, principal.getName());
		
		List<Card> dailyCards = StreamSupport.stream(cards.spliterator(), false)
			    .collect(Collectors.toList());
		
		LearningCardsNumbers cardNumbers = deckService.calculateCardNumbers(dailyCards, deckId, principal.getName());
		
		return new ResponseEntity<LearningCardsNumbers>(cardNumbers, HttpStatus.OK);
	}
	
	
	@GetMapping("/cards/{cardId}")
	public ResponseEntity<?> getCardById(@PathVariable Long cardId, Principal principal){
		
		Card card = cardService.findCardById(cardId, principal.getName());
		
		return new ResponseEntity<Card>(card, HttpStatus.OK);
	}

	@GetMapping("/{deckId}/cards/{cardId}")
	public ResponseEntity<?> getCardByIdForSpecificDeck(@PathVariable Long deckId, @PathVariable Long cardId, Principal principal) {

		Card theCard = cardService.getSingleCardForSpecificDeck(deckId, cardId, principal.getName());

		return new ResponseEntity<Card>(theCard, HttpStatus.OK);
	}

	@GetMapping("/{deckId}/cards/{searchPattern}/all")
	public List<Card> getCardsBySearchPattern(@PathVariable Long deckId, @PathVariable String searchPattern, Principal principal) {

		return cardService.getCardsBySearchPattern(deckId, searchPattern, principal.getName());
	}
	
	@PatchMapping("/{deckId}/{cardId}/learned/{difficulty}")
	public ResponseEntity<?> modifyLearnedCard(@Valid @RequestBody Card card, BindingResult result, @PathVariable Long deckId, @PathVariable Long cardId,@PathVariable String difficulty, Principal principal){

		boolean newlyLarned = card.getCardStatus() == 0 ? true : false;
		
		if(difficulty.equalsIgnoreCase("Again")) {
			card.setDaysTillNextReview(0);
			card.setAgainCounter(card.getAgainCounter() + 1);
		}else if(difficulty.equalsIgnoreCase("Hard")) {
			card.setDaysTillNextReview(card.getHardReview());
		}else if(difficulty.equalsIgnoreCase("Medium")) {
			card.setDaysTillNextReview(card.getMediumReview());
		}else if(difficulty.equalsIgnoreCase("Easy")) {
			card.setDaysTillNextReview(card.getEasyReview());
		}
		
		if(!difficulty.equalsIgnoreCase("Again")) {
			card.setAgainCounter(0);
		}

		card.setCardStatus(difficulty.equalsIgnoreCase("Again") ? (byte) 1 : (byte) 2);
		
		card.setDueDate(cardService.calculateDueDate(deckId, card.getId(), card.getDaysTillNextReview(), card.getCardStatus(), principal.getName()));

		ResponseEntity<?> toReturn = updateCard(card, result, deckId, cardId, principal);
		
		if(newlyLarned) {
			Deck deck = deckService.findDeckById(deckId, principal.getName());
			deck.setCompletedCardsForTheDay(deck.getCompletedCardsForTheDay() + 1);
			deckService.createOrUpdateDeck(deck, principal.getName());
		}
		
		return toReturn;
	}

	@PatchMapping("/{deckId}/cards/{cardId}")
	public ResponseEntity<?> updateCard(@Valid @RequestBody Card card, BindingResult result, @PathVariable Long deckId, @PathVariable Long cardId, Principal principal) {

		ResponseEntity<?> errors = validationService.validate(result);

		if (errors != null) {
			return errors;
		}

		Card updatedCard = cardService.updateCardById(card, deckId, cardId, principal.getName());

		return new ResponseEntity<Card>(updatedCard, HttpStatus.OK);
	}
	
	@DeleteMapping("/{deckId}/cards/{cardId}")
	public ResponseEntity<?> deleteCard(@PathVariable Long deckId, @PathVariable Long cardId, Principal principal){
		cardService.removeCardById(deckId, cardId, principal.getName());
		
		return new ResponseEntity<String>("Card with id '" + cardId + "' has successfully been deleted", HttpStatus.OK);
	}
}
