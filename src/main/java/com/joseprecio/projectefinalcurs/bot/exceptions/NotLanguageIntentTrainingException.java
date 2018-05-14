package com.joseprecio.projectefinalcurs.bot.exceptions;

public class NotLanguageIntentTrainingException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotLanguageIntentTrainingException(String intent, String language) {
		super("Not language training file for intent: '" + intent + "' for language '" + language + "'");
	}
	
}
