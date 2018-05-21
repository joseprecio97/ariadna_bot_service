package com.joseprecio.projectefinalcurs.service;

import java.util.HashMap;
import java.util.Properties;

import com.joseprecio.projectefinalcurs.bot.Intent;
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
	
	public HashMap<String, Intent> getIntents();

	public String[] getAvailableLanguages();

	public void newIntent(Intent newIntent) throws Exception;

	public Intent getIntent(String id);

	public void saveIntent(Intent editIntent) throws Exception;

	public HashMap<String, Properties> getPrompts();

	public void savePrompts() throws Exception;

	public void removeIntent(Intent newIntent) throws Exception;
	
}
