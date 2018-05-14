package com.joseprecio.projectefinalcurs.serviceimpl;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joseprecio.projectefinalcurs.bot.Bot;
import com.joseprecio.projectefinalcurs.bot.exceptions.NotLanguageIntentTrainingException;
import com.joseprecio.projectefinalcurs.bot.exceptions.NotParameterIntentException;
import com.joseprecio.projectefinalcurs.model.CommandReceivedModel;
import com.joseprecio.projectefinalcurs.model.CommandResponseModel;
import com.joseprecio.projectefinalcurs.service.CommandService;

/**
 * Implementación del servico comandos de voz
 * 
 * @author joseprecio
 *
 */

@Service("CommandService")
public class CommandServiceImpl implements CommandService {

	@Autowired
	private Bot bot;
	
	/**
	 * Envia al bot un comando
	 * @throws NotParameterIntentException 
	 * @throws NotParameterPromptException 
	 * @throws NotLanguageIntentTrainingException 
	 * @throws ScriptException 
	 * @throws BadLanguageException 
	 */
	@Override
	public CommandResponseModel sendCommand(CommandReceivedModel command) {
		CommandResponseModel response = bot.sendMessage(command);
		
		//Devolvemos la respuesta del bot
		return response;
	}

}
