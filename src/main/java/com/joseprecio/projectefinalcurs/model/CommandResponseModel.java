package com.joseprecio.projectefinalcurs.model;

import java.io.Serializable;

/**
 * Modelo de respuesta a un commando
 * 
 * @author joseprecio
 *
 */

public class CommandResponseModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String command;
	private String conversationId;

	public CommandResponseModel() {

	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
