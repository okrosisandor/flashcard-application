package com.flashcard.flashcard.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.flashcard.flashcard.entity.Deck;

public interface DeckRepository extends CrudRepository<Deck, Long>{

	public Iterable<Deck> findAllByUsername(String username);
	
	@Modifying
	@Query("UPDATE Deck d SET d.completedCardsForTheDay = 0 WHERE d.id > 0")
	public void resetDailyLearnedCards();
}
