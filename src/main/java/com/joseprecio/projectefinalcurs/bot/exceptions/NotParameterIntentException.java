package com.joseprecio.projectefinalcurs.bot.exceptions;

/**
 * Excepción al no tener en el intent un parametro que el bot está entrenado a identificar
 * 
 * @author joseprecio
 *
 */

public class NotParameterIntentException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NotParameterIntentException(String parameter, String intent, String command) {
		super("Not parameter id: '" + parameter + "' expected for intent: '" + intent + "' detected on command: '" + command + "'");
	}

}
