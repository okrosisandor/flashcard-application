package com.flashcard.flashcard.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Deck {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Deck name is required")
	private String deckName;
	
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, mappedBy = "deck", orphanRemoval = true)
	private List<Card> cards = new ArrayList<Card>();
	
	@Min(value = 0, message = "Daily cards must not be below 0")
	private int dailyCards;
	
	private int completedCardsForTheDay;
	
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_numbers_id", referencedColumnName = "id")
	private LearningCardsNumbers cardNumbers;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User user;
	
	private String username;

	public Deck() {

	}

	public Deck(@NotBlank(message = "Deck name is required") String deckName, List<Card> cards,
			@Min(value = 0, message = "Daily cards must not be below 0") int dailyCards, int completedCardsForTheDay, String username) {
		super();
		this.deckName = deckName;
		this.cards = cards;
		this.dailyCards = dailyCards;
		this.completedCardsForTheDay = completedCardsForTheDay;
		this.username = username;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeckName() {
		return deckName;
	}

	public void setDeckName(String deckName) {
		this.deckName = deckName;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public int getDailyCards() {
		return dailyCards;
	}

	public void setDailyCards(int dailyCards) {
		this.dailyCards = dailyCards;
	}

	public int getCompletedCardsForTheDay() {
		return completedCardsForTheDay;
	}

	public void setCompletedCardsForTheDay(int completedCardsForTheDay) {
		this.completedCardsForTheDay = completedCardsForTheDay;
	}
	
	public LearningCardsNumbers getCardNumbers() {
		return cardNumbers;
	}

	public void setCardNumbers(LearningCardsNumbers cardNumbers) {
		this.cardNumbers = cardNumbers;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = this.user.getUsername();
	}

	@Override
	public String toString() {
		return "Deck [id=" + id + ", deckName=" + deckName + ", dailyCards=" + dailyCards + ", completedCardsForTheDay="
				+ completedCardsForTheDay + "]";
	}

	
}
