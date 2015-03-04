package com.SomeRobot.FlashCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.Log;

public class CardManager {

	RandomHash rh;
	DatabaseHandler db;
	List<CardData> cardDeck = new ArrayList<CardData>();
	Random rand = new Random();

	//Holds the current deck of cards
	public CardManager(DatabaseHandler databaseHandler) {
		db = databaseHandler;
		cardDeck = db.getCardData();
		rh = new RandomHash();
	}

	public CardFragment getCardFragment() {

		//int card = rh.nextInt() % cardData.size();
		
	    int cardnum = rand.nextInt(cardDeck.size());

	    CardData card = cardDeck.get(cardnum);

	   // Log.d("getCardFragment()","Card: " + cardnum +  ", Question: " + card.getQuestion() + ", Answer : " + card.getAnswer());	    
	    
		return new CardFragment(card);//CardFragment.newInstance(cardData.get(card));

	}
	
	public List<CardData> getCardStack() {

		cardDeck = db.getCardData();

		return cardDeck;
	}

	public List<CardData> getCardStack(String s) {

		String[] tags = s.split(",");
		Log.d("getCardStack(s)",Arrays.toString(tags));

		cardDeck = db.getCardData(tags);

		return cardDeck;
	}
}
