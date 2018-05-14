package com.joseprecio.projectefinalcurs.model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modelo de comando recibido por POST
 * 
 * @author joseprecio
 *
 */

public class CommandReceivedModel {

	@NotEmpty
	private String command;
	private String conversationId;
	@NotEmpty
	private String language;

	
	
	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	public CommandReceivedModel() {}
	
}
