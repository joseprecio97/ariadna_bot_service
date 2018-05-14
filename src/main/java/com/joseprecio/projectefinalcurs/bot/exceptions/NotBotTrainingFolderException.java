package com.joseprecio.projectefinalcurs.bot.exceptions;

public class NotBotTrainingFolderException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotBotTrainingFolderException(String configXML) {
		super(configXML + ": Not Bot training folder found on the current directory");
	}
	
}
