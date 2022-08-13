package com.flashcard.flashcard.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flashcard.flashcard.entity.Card;
import com.flashcard.flashcard.entity.Deck;
import com.flashcard.flashcard.entity.LearningCardsNumbers;
import com.flashcard.flashcard.entity.User;
import com.flashcard.flashcard.exception.DecknameAlreadyExistsException;
import com.flashcard.flashcard.exception.IdException;
import com.flashcard.flashcard.exception.NotFoundException;
import com.flashcard.flashcard.repository.DeckRepository;
import com.flashcard.flashcard.repository.UserRepository;

@Service
@EnableScheduling
public class DeckService {

	@Autowired
	private DeckRepository deckRepository;
	
    @Autowired
    private UserRepository userRepository;

	public Deck createOrUpdateDeck(Deck deck, String username) {
		
        User user = userRepository.findByUsername(username);
        
    	Iterable<Deck> decks = getDecks(username);
    	
    	List<Deck> decksList = StreamSupport.stream(decks.spliterator(), false).collect(Collectors.toList());
    	
    	for(Deck theDeck : decksList) {
    		if(theDeck.getDeckName().equals(deck.getDeckName()) && !theDeck.getId().equals(deck.getId())) {
    			throw new DecknameAlreadyExistsException("There is already one deck with this name.");
    		}
    	}
        
        deck.setUser(user);
        deck.setUsername(username);
		
		//Only return null as cards when there is no card in case of deck updating
		if(deck.getId() != null) {
			deck.setCards(findDeckById(deck.getId(), username).getCards());
		}

		return deckRepository.save(deck);
	}

	public Iterable<Deck> getDecks(String username) {

		return deckRepository.findAllByUsername(username);
	}

	public Deck findDeckById(Long deckId, String username) {

		Optional<Deck> result = deckRepository.findById(deckId);

		Deck deck = null;

		if (result.isPresent()) {
			deck = result.get();
		} else {
			throw new IdException("Could not find deck with id '" + deckId + "'. It may not exist.");
		}
		
		if(!deck.getUser().getUsername().equals(username)) {
			throw new NotFoundException("Deck could not be found in your account");
		}

		return deck;
	}

	public void removeDeckById(Long deckId, String username) {
		
		deckRepository.delete(findDeckById(deckId, username));
		
	}
	
	public LearningCardsNumbers calculateCardNumbers(List<Card> dailyCards, Long deckId, String username) {
		
		LearningCardsNumbers cardNumbers = new LearningCardsNumbers();
		
		int newCards = dailyCards.stream()
				.filter(card -> card.getCardStatus() == 0).collect(Collectors.toList()).size();
		
		int progressCards = dailyCards.size() - newCards;
		
		int totalCards = findDeckById(deckId, username).getCards().size();
		
		cardNumbers.setNewCards(newCards);
		cardNumbers.setDailyCards(progressCards);
		cardNumbers.setTotalCards(totalCards);
		
		return cardNumbers;
	}
	
	
	@Scheduled(cron = "0 0 0 * * ?")
	@Transactional
	public void resetDailyLearnedCards() {
		deckRepository.resetDailyLearnedCards();
	}
}
