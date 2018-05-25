package com.joseprecio.projectefinalcurs.serviceimpl;

import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joseprecio.projectefinalcurs.bot.Bot;
import com.joseprecio.projectefinalcurs.bot.BotConstants;
import com.joseprecio.projectefinalcurs.bot.Intent;
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
	 * 
	 * @throws NotParameterIntentException 
	 * @throws NotParameterPromptException 
	 * @throws NotLanguageIntentTrainingException 
	 * @throws ScriptException 
	 * @throws BadLanguageException 
	 */
	@Override
	public CommandResponseModel sendCommand(CommandReceivedModel command) {
		CommandResponseModel response = bot.sendMessage(command);
		
		response.setConversationId(command.getConversationId());
		
		//Devolvemos la respuesta del bot
		return response;
	}

	/**
	 * Devuelve los intents que tiene el bot creados
	 */
	@Override
	public HashMap<String, Intent> getIntents() {
		return bot.getIntents();
	}

	/**
	 * Devuelve los lenguajes disponibles del bot
	 */
	@Override
	public String[] getAvailableLanguages() {
		return BotConstants.BOT_AVAILABLE_LANGUAGE;
	}
	
	/**
	 * Crea un nuevo intent en el bot
	 */
	@Override
	public void newIntent(Intent newIntent) throws Exception {
		//Añadimos el intent a los intents del bot
		bot.addIntent(newIntent);
		
		//Guardamos los intents en el json
		bot.saveIntents();
		
		//Guardamos los prompts en disco
		bot.savePrompts();
	}
	
	/**
	 * Elimina un intent
	 */
	@Override
	public void removeIntent(Intent newIntent) throws Exception {
		//Eliminamos el intent
		bot.removeIntent(newIntent.getId());
		
		//Guardamos los intents en el json
		bot.saveIntents();
	}
	
	/**
	 * Guarda los prompts en disco
	 * 
	 * @throws Exception
	 */
	@Override
	public void savePrompts() throws Exception {
		//Guardamos los prompts en disco
		bot.savePrompts();
	}
	
	/**
	 * Devuelve los prompts del bot
	 * 
	 * @return
	 */
	@Override
	public HashMap<String, Properties> getPrompts(){
		return bot.getLanguagePrompts();
	}
	
	/**
	 * Devuelve un intent a partir de un id
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Intent getIntent(String id) {
		return bot.getIntent(id);
	}
	
	/**
	 * Actualiza un intent
	 * 
	 * @param editIntent
	 * @throws Exception
	 */
	@Override
	public void saveIntent(Intent editIntent) throws Exception {
		//Guardamos el intent en el hash map
		bot.saveIntent(editIntent);
		
		//Guardamos los intents en el fichero json de configuración
		bot.saveIntents();
	}
	
	/**
	 * Crea un nuevo id de conversación
	 * 
	 * @return
	 */
	@Override
	public String getNewConversationId() {
		String randomConversationId = null;
		
		do {
			//Generamos un id de conversación aleatorio
			randomConversationId = UUID.randomUUID().toString();
		}while(!bot.validGeneratedConversationId(randomConversationId));
		
		//Devolvemos el id de conversación
		return randomConversationId;
	}
	
}
