package com.SomeRobot.FlashCard;

public class CardData {

	String question, answer, tags;
	
	public CardData(String question, String answer, String tags) {
		this.question = question;
		this.answer = answer;
		this.tags = tags;
	}
	
	public String getQuestion() {
		return question;
	}
	public String getAnswer() {
		return answer;
	}
	public String getTags() {
		return tags;
	}
	public String toString() {
		
		String s = "Question: " + question + ", Answer: " + answer + ", Tags: " + tags;
		
		return s.replaceAll("\r", " ").replaceAll("\n", " ");
	}
}
