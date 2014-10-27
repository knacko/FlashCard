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
	List<CardData> cardData = new ArrayList<CardData>();

	//Holds the current deck of cards
	public CardManager(DatabaseHandler databaseHandler) {
		db = databaseHandler;
		cardData = db.getCardData();
		rh = new RandomHash();
	}

	public CardFragment getCardFragment() {

		//int card = rh.nextInt() % cardData.size();
		Random rand = new Random();
	    int card = rand.nextInt(cardData.size());

		String question = cardData.get(card).getQuestion();
		String answer = cardData.get(card).getAnswer();

		return CardFragment.newInstance(question, answer);

	}
	

	public List<CardData> getCardStack() {

		cardData = db.getCardData();

		return cardData;
	}

	public List<CardData> getCardStack(String s) {

		String[] tags = s.split(",");
		Log.d("getCardStack(s)",Arrays.toString(tags));

		cardData = db.getCardData(tags);

		return cardData;
	}
}
