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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	private CommandReceivedModel() {}
	
}
