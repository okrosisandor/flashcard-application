package com.flashcard.flashcard.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Front is required")
	private String front;
	
	@NotBlank(message = "Back is required")
	private String back;
	
	private int daysTillNextReview;
	
	private int hardReview;
	private int mediumReview;
	private int easyReview;
	
	
	private Date dueDate;
	
	private byte cardStatus;
	
	private int againCounter;
	
	@ManyToOne(fetch = FetchType.EAGER)
	//Name of foreign key
	@JoinColumn(name = "deck_id")
	@JsonIgnore
	private Deck deck;
	
	private Long theDeckId;
	
	private String theDeckName;

	public Card() {
		
	}
	
	public Card(@NotBlank(message = "Front is required") String front,
			@NotBlank(message = "Back is required") String back, Date dueDate, byte cardStatus,
			@NotBlank(message = "Select your deck") Deck deck) {
		super();
		this.front = front;
		this.back = back;
		this.dueDate = dueDate;
		this.cardStatus = cardStatus;
		this.deck = deck;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFront() {
		return front;
	}

	public void setFront(String front) {
		this.front = front;
	}

	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Deck getDeck() {
		return deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public byte getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(byte cardStatus) {
		this.cardStatus = cardStatus;
	}
	
	public int getDaysTillNextReview() {
		return daysTillNextReview;
	}

	public void setDaysTillNextReview(int daysTillNextReview) {
		this.daysTillNextReview = daysTillNextReview;
	}
	
	public String getTheDeckName() {
		return deck.getDeckName();
	}

	public Long getTheDeckId() {
		return deck.getId();
	}
	
	public int getAgainCounter() {
		return againCounter;
	}

	public void setAgainCounter(int againCounter) {
		this.againCounter = againCounter;
	}

	public void setTheDeckId(Long theDeckId) {
		this.theDeckId = theDeckId;
	}

	public void setTheDeckName(String theDeckName) {
		this.theDeckName = theDeckName;
	}
	
	public int getHardReview() {
		return hardReview;
	}

	public void setHardReview(int hardReview) {
		this.hardReview = hardReview;
	}

	public int getMediumReview() {
		return mediumReview;
	}

	public void setMediumReview(int mediumReview) {
		this.mediumReview = mediumReview;
	}

	public int getEasyReview() {
		return easyReview;
	}

	public void setEasyReview(int easyReview) {
		this.easyReview = easyReview;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", front=" + front + ", back=" + back + ", daysTillNextReview=" + daysTillNextReview
				+ ", hardReview=" + hardReview + ", mediumReview=" + mediumReview + ", easyReview=" + easyReview
				+ ", dueDate=" + dueDate + ", cardStatus=" + cardStatus + ", againCounter=" + againCounter + "]";
	}

	
	
	
	
	
	
	

	
	
	
}
