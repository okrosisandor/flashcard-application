package com.flashcard.flashcard.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.flashcard.flashcard.entity.Card;
import com.flashcard.flashcard.entity.Deck;
import com.flashcard.flashcard.exception.IdException;
import com.flashcard.flashcard.exception.NoDeckException;
import com.flashcard.flashcard.exception.NotFoundException;
import com.flashcard.flashcard.repository.CardRepository;
import com.flashcard.flashcard.repository.DeckRepository;

@Service
public class CardService {

	@Autowired
	private DeckService deckService;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private DeckRepository deckRepository;
	
	public Card createCard(Card card, Long deckId, String username) {

		Deck theDeck = deckService.findDeckById(deckId, username);

		card.setCardStatus((byte) 0);

		card.setDeck(theDeck);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dateWithoutTime = sdf.parse(sdf.format(new Date()));
			card.setDueDate(dateWithoutTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		card.setTheDeckId(theDeck.getId());
		card.setTheDeckName(theDeck.getDeckName());

		return cardRepository.save(card);
	}
	
	public List<Card> saveMultipleCards(MultipartFile uploadedFile, Long deckId, String username) {
		try {
		      InputStream stream = uploadedFile.getInputStream();
		      BufferedReader br = new BufferedReader(new InputStreamReader(uploadedFile.getInputStream(), "UTF-8"));

		      String line = br.readLine();
		      List<Card> cards = new ArrayList<Card>();
		      
		      Card card = new Card();
		      
		      String[] sentences = line.split("\\t");
		      card.setFront(sentences[0]);
		      card.setBack(sentences[1]);
		      
		      cards.add(createCard(card, deckId, username));
		      
		      while((line = br.readLine()) != null) {
		    	  
		    	  	card = new Card();
		    	  	sentences = line.split("\\t");
				    card.setFront(sentences[0]);
				    card.setBack(sentences[1]);
				    
				    cards.add(createCard(card, deckId, username));
				}
		      
		      return cards;
		      
		    } catch (Exception e) {
		    	throw new RuntimeException("Error when creating cards.");
		    }
	}

	public List<Card> retrieveCardsForDeck(Long deckId, String username) {
		
		return deckService.findDeckById(deckId, username).getCards();
	}

	public Card getSingleCardForSpecificDeck(Long deckId, Long cardId, String username) {

		Deck theDeck = deckService.findDeckById(deckId, username);

		List<Card> result = theDeck.getCards().stream().filter(c -> c.getId().equals(cardId)).collect(Collectors.toList());

		// List is either empty or has exactly 1 element
		if (result.isEmpty()) {
			throw new IdException("Could not find cardId '" + cardId + "' in deck, with id '" + deckId + "'");
		}

		return result.get(0);
	}

	public List<Card> getCardsBySearchPattern(Long deckId, String searchPattern, String username) {
		List<Card> result = new ArrayList<Card>();
		
		searchPattern = searchPattern.trim();

		if (deckId == 0) {
			List<Deck> allDecks = (List<Deck>) deckService.getDecks(username);

			if (allDecks.isEmpty()) {
				throw new NoDeckException("");
			}
			
			for (int i = 0; i < allDecks.size(); i++) {
				result = checkForMatch(allDecks.get(i), result, searchPattern);
			}
		} else {
			// Here it is not neccessary to check whether there are any decks, since at this
			// point we received a valid deck id, thereby there must be at least 1 deck
			Deck theDeck = deckService.findDeckById(deckId, username);
			result = checkForMatch(theDeck, result, searchPattern);
		}

		return result;
	}

	private List<Card> checkForMatch(Deck deck, List<Card> result, String searchPattern) {
		
		String pattern = searchPattern.toLowerCase();

		for (Card card : deck.getCards()) {
			
			if(pattern.equalsIgnoreCase("empty-search")) {
				result.add(card);
			}
			else if (card.getFront().toLowerCase().contains(pattern) || card.getBack().toLowerCase().contains(pattern)) {
				result.add(card);
			}
		}

		return result;
	}

	public Iterable<Card> retrieveCardsForLearning(Long deckId, String username) {

		Deck deck = deckService.findDeckById(deckId, username);

		List<Card> review = getReviewCards(deck, deckId);
		List<Card> inProgress = getInProgressCards(deck, deckId);
		List<Card> newCards = getNewCards(deck, deckId);

		List<Card> toReturn = new ArrayList<Card>();
		toReturn.addAll(review);
		toReturn.addAll(inProgress);
		toReturn.addAll(newCards);
		
		for(Card card : toReturn) {
			int nextReview = card.getDaysTillNextReview();
			if(nextReview < 10) {
				card.setHardReview(nextReview + 1);
				card.setMediumReview(nextReview + 2);
				card.setEasyReview(nextReview + 4);
			}else {
				card.setHardReview(nextReview + (int) Math.floor(nextReview * 0.25));
				card.setMediumReview(nextReview + (int) Math.floor(nextReview * 0.5));
				card.setEasyReview(nextReview * 2);
			}

			cardRepository.save(card);
		}

		return toReturn;
	}

	private List<Card> getNewCards(Deck deck, Long deckId) {
		if (deck.getCompletedCardsForTheDay() >= deck.getDailyCards()) {
			return new ArrayList<Card>();
		}

		int remainingCardsToLearnForTheDay = deck.getDailyCards() - deck.getCompletedCardsForTheDay();

		List<Card> cardsToLearn = new ArrayList<Card>();

		List<Card> notLearnedCards = cardRepository.getCardsForLearning((byte) 0, deckId);

		// If less card available for learning than the dailyLimit
		if (notLearnedCards.size() <= remainingCardsToLearnForTheDay) {
			return notLearnedCards;
		}

		for (int i = 0; i < remainingCardsToLearnForTheDay; i++) {
			Card currentCard = notLearnedCards.get(i);

			cardsToLearn.add(currentCard);
		}

		return cardsToLearn;
	}

	private List<Card> getInProgressCards(Deck deck, Long deckId) {

		List<Card> inProgressCards = cardRepository.getInProgressCards((byte) 1, deckId);

		return inProgressCards;
	}

	private List<Card> getReviewCards(Deck deck, Long deckId) {

		List<Card> reviewCards = cardRepository.getCardsForReview((byte) 2, deckId);

		return reviewCards;
	}

	public Card updateCardById(@Valid Card updatedCard, Long deckId, Long cardId, String username) {
		
		Card theCard = getSingleCardForSpecificDeck(deckId, cardId, username);
		
		theCard = updatedCard;
		
		theCard.setDeck(deckService.findDeckById(deckId, username));
		
		return cardRepository.save(theCard);
	}

	public void removeCardById(Long deckId, Long cardId, String username) {
		Card card = getSingleCardForSpecificDeck(deckId, cardId, username);
		
		cardRepository.delete(card);
		
	}

	public Card findCardById(Long cardId, String username) {
		
		Optional<Card> result = cardRepository.findById(cardId);

		Card card = null;

		if (result.isPresent()) {
			card = result.get();
		} else {
			throw new IdException("Could not find card with id '" + cardId + "'");
		}
		
		if(!card.getDeck().getUser().getUsername().equals(username)) {
			throw new NotFoundException("Card could not be found in your account. It may not exist.");
		}

		return card;
	}

	@Transactional
	public @Valid Date calculateDueDate(Long deckId, Long cardId, int days, byte cardStatus, String username) {
		
		Card card = findCardById(cardId, username);
		
		Date date = new Date();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		
		Date modifiedDate = cal.getTime();
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date newDueDate = sdf.parse(sdf.format(modifiedDate));
			card.setDueDate(newDueDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return updateCardById(card, deckId, cardId, username).getDueDate();
	}

}
