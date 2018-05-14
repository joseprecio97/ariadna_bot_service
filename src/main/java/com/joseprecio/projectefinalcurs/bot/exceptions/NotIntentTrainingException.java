package com.joseprecio.projectefinalcurs.bot.exceptions;

public class NotIntentTrainingException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotIntentTrainingException(String intent) {
		super("Not training file for intent: '" + intent + "'");
	}
	
}
