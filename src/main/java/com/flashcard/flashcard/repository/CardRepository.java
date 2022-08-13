package com.flashcard.flashcard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.flashcard.flashcard.entity.Card;

@Repository
public interface CardRepository extends CrudRepository<Card, Long>{

	@Query("SELECT c FROM Card c WHERE c.cardStatus = ?1 and c.deck.id = ?2")
	List<Card> getCardsForLearning(byte status, Long deckId);
	
	@Query("SELECT c FROM Card c WHERE c.cardStatus = ?1 and c.deck.id = ?2 and c.dueDate <= CURRENT_TIMESTAMP")
	List<Card> getCardsForReview(byte status, Long deckId);
	
	@Query("SELECT c FROM Card c WHERE c.cardStatus = ?1 and c.deck.id = ?2 order by c.againCounter")
	List<Card> getInProgressCards(byte b, Long deckId);

	@Modifying
	@Query("UPDATE Card c SET c.dueDate = DATEADD('DAY', ?2, c.dueDate) WHERE c.id = ?1")
	public void calculateDueDate(Long cardId, int days);

}
