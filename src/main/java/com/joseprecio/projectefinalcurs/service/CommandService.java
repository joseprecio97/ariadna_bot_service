package com.joseprecio.projectefinalcurs.service;

import com.joseprecio.projectefinalcurs.model.CommandReceivedModel;
import com.joseprecio.projectefinalcurs.model.CommandResponseModel;

/**
 * Servicio de commandos
 * 
 * @author joseprecio
 *
 */

public interface CommandService {

	public CommandResponseModel sendCommand(CommandReceivedModel command);
	
}
