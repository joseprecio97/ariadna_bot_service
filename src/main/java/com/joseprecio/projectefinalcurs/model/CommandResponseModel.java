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
	private boolean serverError;
	
	public CommandResponseModel() {
		this.serverError = false;
	}

	public boolean isServerError() {
		return serverError;
	}

	public void setServerError(boolean serverError) {
		this.serverError = serverError;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
