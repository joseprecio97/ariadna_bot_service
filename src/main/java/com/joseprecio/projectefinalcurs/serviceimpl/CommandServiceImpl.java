package com.joseprecio.projectefinalcurs.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Locale.Category;

import javax.script.ScriptException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.joseprecio.projectefinalcurs.ApplicationConstants;
import com.joseprecio.projectefinalcurs.bot.Bot;
import com.joseprecio.projectefinalcurs.bot.BotConstants;
import com.joseprecio.projectefinalcurs.bot.Intent;
import com.joseprecio.projectefinalcurs.bot.Parameter;
import com.joseprecio.projectefinalcurs.bot.exceptions.NotLanguageIntentTrainingException;
import com.joseprecio.projectefinalcurs.bot.exceptions.NotParameterIntentException;
import com.joseprecio.projectefinalcurs.model.CommandReceivedModel;
import com.joseprecio.projectefinalcurs.model.CommandResponseModel;
import com.joseprecio.projectefinalcurs.service.CommandService;

import opennlp.maxent.GISModel;
import opennlp.model.MutableContext;
import opennlp.model.UniformPrior;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.namefind.DefaultNameContextGenerator;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.featuregen.AggregatedFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;

/**
 * Implementación del servico comandos de voz
 * 
 * @author joseprecio
 *
 */

@Service("CommandService")
public class CommandServiceImpl implements CommandService {

	@Autowired
	@Qualifier("bot")
	private Bot bot;
	
	@Autowired
	@Qualifier("bot_production")
	private Bot bot_production;
	
	/**
	 * Actualiza el Bot de producción
	 * @throws Exception 
	 */
	@Override
	public void deploy() throws Exception {
		try {
			//Obtenemos los mensajes de log
			ResourceBundle loggerResources = ResourceBundle.getBundle(ApplicationConstants.TEXTS_FILENAME,
					Locale.getDefault(Category.DISPLAY));
			
			//Copiamos el directorio del bot de desarrollo como el bot de producción
			org.apache.commons.io.FileUtils.copyDirectory(new File(BotConstants.BOT_CONFIG_FOLDER), 
					new File(BotConstants.BOT_DEPLOY_FOLDER));
			
			//Creamos un Bot
			Bot bot_temp = new Bot();
			bot_temp.setProduction(true);
			
			// Inicializamos el bot
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ MessageFormat.format(loggerResources.getString("msg_init_bot_intentTraining_load"), "PROD"));
			bot_temp.init();
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ MessageFormat.format(loggerResources.getString("msg_end_bot_intentTraining_load"), "PROD"));
			bot_production.setError(false);
			//Si no se producce ningún error durante el entrenamiento, actualizamos el Bot de producción
			bot_production = bot_temp;
		}catch(Exception e) {
			//Pintamos la excepción
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	/**
	 * Entrena el Bot de desarrollo
	 * @throws Exception 
	 */
	@Override
	public void train() throws Exception {
		//Obtenemos los mensajes de log
		ResourceBundle loggerResources = ResourceBundle.getBundle(ApplicationConstants.TEXTS_FILENAME,
				Locale.getDefault(Category.DISPLAY));
		
		try {
			//Creamos un Bot
			Bot bot_temp = new Bot();
			bot_temp.setProduction(false);
			
			// Inicializamos el bot
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ MessageFormat.format(loggerResources.getString("msg_init_bot_intentTraining_load"), "DEV"));
			bot_temp.init();
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ MessageFormat.format(loggerResources.getString("msg_end_bot_intentTraining_load"), "DEV"));
			bot.setError(false);
			//Si no se producce ningún error durante el entrenamiento, actualizamos el Bot de desarrollo
			bot = bot_temp;
		} catch (Exception e) {
			// Mostramos la traza de error
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
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
	public CommandResponseModel sendCommand(CommandReceivedModel command, boolean productionBot) {
		CommandResponseModel response = null;
		
		if(productionBot) {
			response = bot_production.sendMessage(command);
		}else {
			response = bot.sendMessage(command);
		}
		
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
	public String getNewConversationId(boolean productionBot) {
		String randomConversationId = null;
		
		if(productionBot) {
			do {
				//Generamos un id de conversación aleatorio
				randomConversationId = UUID.randomUUID().toString();
			}while(!bot_production.validGeneratedConversationId(randomConversationId));
		}else {
			do {
				//Generamos un id de conversación aleatorio
				randomConversationId = UUID.randomUUID().toString();
			}while(!bot.validGeneratedConversationId(randomConversationId));
		}
		
		//Devolvemos el id de conversación
		return randomConversationId;
	}
	
}
