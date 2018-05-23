package com.joseprecio.projectefinalcurs.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.joseprecio.projectefinalcurs.model.CommandReceivedModel;
import com.joseprecio.projectefinalcurs.model.CommandResponseModel;
import com.joseprecio.projectefinalcurs.model.googleassistant.GoogleAssistantCommandModel;
import com.joseprecio.projectefinalcurs.model.googleassistant.GoogleAssistantResponseModel;
import com.joseprecio.projectefinalcurs.service.CommandService;

/**
 * Controlador de comandos de voz
 * 
 * @author joseprecio
 *
 */

@RestController
@RequestMapping("/api/v1/")
public class CommandController {

	@Autowired
	@Qualifier("CommandService")
	private CommandService commandServiceImpl;

	/**
	 * Recibe las peticiones de Google Assistant
	 * 
	 * @param commandReceived
	 * @return
	 */
	@PostMapping("/googleassistant")
	private ResponseEntity<String> googleAssistantCommand(@RequestBody String commandReceived){
		//Objeto JSON
		Gson json = new Gson();
		
		System.out.println("JOSEP - GOOGLE ACTIONS POST: " + commandReceived);
		
		/*
		//Creamos el modelo de mensaje de Ariadna Bot Service
		CommandReceivedModel ariadnaCommandModel = new CommandReceivedModel();
		
		//Establecemos los atributos
		ariadnaCommandModel.setCommand(commandReceived.getQueryResult().getQueryText());
		ariadnaCommandModel.setConversationId(commandReceived.getSession());
		ariadnaCommandModel.setLanguage(commandReceived.getQueryResult().getLanguageCode().substring(0, 2));
		
		// Ejecutamos el comando
		CommandResponseModel response = commandServiceImpl.sendCommand(ariadnaCommandModel);
		
		//Creamos una respuesta para google assistant a partir de la respuesta de bot Ariadna
		GoogleAssistantResponseModel googleAssistantResponse = new GoogleAssistantResponseModel();
		
		//Establecemos el texto de la respuesta
		googleAssistantResponse.setFulfillmentText(response.getCommand());
		
		*/
		
		//Devolvemos la respuesta
		return ResponseEntity.ok().body(json.toJson(""));
	}
	
	/**
	 * Commando propio de ariadna bot service
	 * 
	 * @param commandReceived
	 * @param bindingResult
	 * @return ResponseEntity
	 */
	@PostMapping("/command")
	private ResponseEntity<String> doSomething(@Valid @RequestBody CommandReceivedModel commandReceived,
			BindingResult bindingResult) {

		Gson json = new Gson();

		// Validamos el modelo recibido
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(json.toJson(bindingResult.getAllErrors()));
		}

		// Ejecutamos el comando
		CommandResponseModel response = commandServiceImpl.sendCommand(commandReceived);

		if(!response.isServerError()) {
			// Devolvemos la respuesta del bot
			return ResponseEntity.ok().body(json.toJson(response));
		}else {
			//Devolvemos la respuesta indicando que se ha producido un error en el servidor
			return ResponseEntity.status(500).body(json.toJson(response));
		}

	}

}
