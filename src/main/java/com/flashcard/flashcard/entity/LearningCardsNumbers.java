package com.flashcard.flashcard.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class LearningCardsNumbers {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int newCards;
	private int dailyCards;
	private int totalCards;
	
	@OneToOne(mappedBy = "cardNumbers")
	private Deck deck;
	
	public LearningCardsNumbers() {
		super();
	}
	
	public LearningCardsNumbers(int newCards, int dailyCards, int totalCards, Deck deck) {
		super();
		this.newCards = newCards;
		this.dailyCards = dailyCards;
		this.totalCards = totalCards;
		this.deck = deck;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getNewCards() {
		return newCards;
	}

	public void setNewCards(int newCards) {
		this.newCards = newCards;
	}

	public int getDailyCards() {
		return dailyCards;
	}

	public void setDailyCards(int dailyCards) {
		this.dailyCards = dailyCards;
	}

	public int getTotalCards() {
		return totalCards;
	}

	public void setTotalCards(int totalCards) {
		this.totalCards = totalCards;
	}


	public Deck getDeck() {
		return deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	@Override
	public String toString() {
		return "LearningCardsNumbers [id=" + id + ", newCards=" + newCards + ", dailyCards=" + dailyCards
				+ ", totalCards=" + totalCards + "]";
	}
	
	
}
