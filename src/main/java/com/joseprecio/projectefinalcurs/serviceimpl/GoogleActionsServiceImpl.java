package com.joseprecio.projectefinalcurs.serviceimpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.joseprecio.projectefinalcurs.bot.BotConstants;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.Actions;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.AriadnaBotService;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.Conversations;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.Fulfillment;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.GoogleActionModel;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.GoogleActionsSDKResponse;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.GoogleActionsSdkInformation;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.Intent;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.Trigger;
import com.joseprecio.projectefinalcurs.service.GoogleActionsService;

@Service("GoogleActionsService")
public class GoogleActionsServiceImpl implements GoogleActionsService {

	@Value("${so}")
	private String so;
	
	@Value("${ariadnabot.url}")
	private String url;
	
	/**
	 * Actualiza el project id
	 */
	@Override
	public void setInfo(GoogleActionsSdkInformation info) throws IOException {
		// Obtenemos el fichero
		FileWriter file = new FileWriter(
				BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_GOOGLEACTIONS_FOLDER + "//" + BotConstants.BOT_GOOGLEACTIONS_FILE, false);
		PrintWriter writer = new PrintWriter(file);

		Gson json = new Gson();
		
		// Guardamos la información en el fichero
		writer.println(json.toJson(info));

		// Cerramos el stream
		writer.close();
		file.close();
	}

	/**
	 * Devuelve la información de Google Actions SDK
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	public GoogleActionsSdkInformation getInfo() throws IOException {
		// Objetos
		String linea = null;
		GoogleActionsSdkInformation googleActionsSdkInformation = null;
		Gson json = new Gson();
		
		// Cargamos el fichero
		File file = new File(
				BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_GOOGLEACTIONS_FOLDER + "//" + 
				BotConstants.BOT_GOOGLEACTIONS_FILE);

		// Leemos el fichero
		BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		while ((linea = b.readLine()) != null) {
			//Creamos el objeto a partir del JSON
			googleActionsSdkInformation = json.fromJson(linea, GoogleActionsSdkInformation.class);
		}

		// Cerramos el buffer
		b.close();

		// Devolvemos la información de google actions
		return googleActionsSdkInformation;
	}

	private void prepareActionsJs() throws IOException {
		//Obtenemos la información de google actions
		GoogleActionsSdkInformation info = getInfo();
		
		//Recorremos todos los lenguajes en que estará disponible el bot en google assistant
		for(String language : info.getLanguages()) {
			ArrayList<Actions> actions = new ArrayList<Actions>();
			
			//Creamos el modelo de google actions
			GoogleActionModel GAModel = new GoogleActionModel();
			
			//Establecemos el idioma
			GAModel.setLocale(language);
			
			//Creamos el objeto que hace referencia a la instancia de Ariadna Bot Service
			AriadnaBotService ariadnaInstance = new AriadnaBotService();
			ariadnaInstance.setName("AriadnaBotService");
			ariadnaInstance.setUrl(url + BotConstants.BOT_GOOGLEASSISTANT_URL);
			Conversations conversations = new Conversations();
			conversations.setAriadnaBotService(ariadnaInstance);
			GAModel.setConversations(conversations);
			
			//WELCOME ACTION
			//Frases
			Trigger welcomeTrigger = new Trigger();
			String[] welcomePhrases = new String[info.getWelcomePhrases().get(language).size()];
			for(int i = 0; i < info.getWelcomePhrases().get(language).size(); i++) {
				welcomePhrases[i] = info.getWelcomePhrases().get(language).get(i);
			}
			
			welcomeTrigger.setQueryPatterns(welcomePhrases); 
			
			//Intent
			Intent welcomeIntent = new Intent();
			welcomeIntent.setTrigger(welcomeTrigger);
			welcomeIntent.setName("actions.intent.MAIN");
			
			//Fulfillment
			Fulfillment welcomeFulfillment = new Fulfillment();
			welcomeFulfillment.setConversationName(ariadnaInstance.getName());
			
			//Action
			Actions welcomeAction = new Actions();
			welcomeAction.setDescription("Default Welcome Intent");
			welcomeAction.setName("MAIN");
			welcomeAction.setFulfillment(welcomeFulfillment);
			welcomeAction.setIntent(welcomeIntent);
			
			//Añadimos la welcome action
			actions.add(welcomeAction);
			
			//MESSAGE ACTION
			//Frases
			Trigger messageTrigger = new Trigger();
			String[] phrases = {"$SchemaOrg_Text"};
			messageTrigger.setQueryPatterns(phrases); 
			
			//Intent
			Intent messageIntent = new Intent();
			messageIntent.setTrigger(welcomeTrigger);
			messageIntent.setName("ariadna.service.MESSAGE");
			
			//Fulfillment
			Fulfillment messageFulfillment = new Fulfillment();
			messageFulfillment.setConversationName(ariadnaInstance.getName());
			
			//Action
			Actions messageAction = new Actions();
			messageAction.setDescription("MESSAGE");
			messageAction.setName("MESSAGE");
			messageAction.setFulfillment(messageFulfillment);
			messageAction.setIntent(messageIntent);
			
			//Añadimos el message action
			actions.add(messageAction);
			
			//Añadimos las acciones al modelo
			Actions[] actionsArray = new Actions[actions.size()];
			for(int i = 0; i < actions.size(); i++) {
				actionsArray[i] = actions.get(i);
			}
			GAModel.setActions(actionsArray);
			
			// Obtenemos el fichero
			FileWriter file = new FileWriter(
					BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_GOOGLEACTIONS_FOLDER + "//" + so
					+ "//" + language + "-action.json", false);
			PrintWriter writer = new PrintWriter(file);

			Gson json = new Gson();
			
			// Guardamos la información en el fichero
			writer.println(json.toJson(GAModel));

			// Cerramos el stream
			writer.close();
			file.close();
		}
	}
	
	/**
	 * Actualiza el proyecto en google actions
	 * 
	 * @param token
	 * @return
	 * @throws Exception 
	 */
	@Override
	public GoogleActionsSDKResponse updateGoogleActions(String token) throws Exception {
		Process pr = null;
		GoogleActionsSDKResponse response = new GoogleActionsSDKResponse();
		GoogleActionsSdkInformation info = this.getInfo();
		
		try {
			//Preparamos los ficheros .json a exportar a google actions
			prepareActionsJs();
			
			//Obtenemos el ejecutable
            File pathToExecutable = new File(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_GOOGLEACTIONS_FOLDER 
            	+ "/" + so + "/gactions");
            
            //Establecemos los parámetros del ejecutable
            ArrayList<String> program = new ArrayList<String>();
            program.add(pathToExecutable.getAbsolutePath());
            program.add("update");
            for(String language : info.getLanguages()) {
            	program.add("--action_package");
            	program.add(language + "-action.json");
            }
            program.add("--project");
            program.add(info.getProjectId());
            
            ProcessBuilder builder = new ProcessBuilder(program);
            
            //Establecemos el directorio del proceso
            builder.directory(new File(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_GOOGLEACTIONS_FOLDER + "/" + so).getAbsoluteFile());
            builder.redirectErrorStream(false);
            
            //Iniciamos el proceso
            pr =  builder.start();

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line=null;
            StringBuilder processOutput = new StringBuilder();
            
            //Comprovamos si hemos recibido un token
            if(token != null) {
            	//Pasamos el token al proceso
            	pr.getOutputStream().write(token.getBytes());
            	pr.getOutputStream().flush();
            	pr.getOutputStream().close();
            }
            
            //Obtenemos la consola del proceso
            while((line=input.readLine()) != null) {
            	processOutput.append(line);
            	processOutput.append(System.lineSeparator());
            }
            
            //Devolvemos la respuesta
            response.setCode(pr.exitValue());
            response.setConsoleMessage(processOutput.toString());
            return response;
        } catch(Exception e) {
        	//Excepción
            throw e;
        }
	}
	
}
