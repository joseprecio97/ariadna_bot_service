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
import com.joseprecio.projectefinalcurs.model.googleassistant.response.ExpectedInputs;
import com.joseprecio.projectefinalcurs.model.googleassistant.response.GoogleAssistantResponseModel;
import com.joseprecio.projectefinalcurs.model.googleassistant.response.InputPrompt;
import com.joseprecio.projectefinalcurs.model.googleassistant.response.Items;
import com.joseprecio.projectefinalcurs.model.googleassistant.response.RichInitialPrompt;
import com.joseprecio.projectefinalcurs.model.googleassistant.response.SimpleResponse;
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
	 * @param postJson
	 * @return
	 */
	@PostMapping("/googleassistant")
	private ResponseEntity<String> googleAssistantCommand(@RequestBody String postJson){
		//Objeto JSON
		Gson json = new Gson();
		
		//Creamos el modelo de google assistant a partir del JSON recibido
		GoogleAssistantCommandModel commandReceived = json.fromJson(postJson, GoogleAssistantCommandModel.class);
		
		//Creamos el modelo de mensaje de Ariadna Bot Service
		CommandReceivedModel ariadnaCommandModel = new CommandReceivedModel();
		
		//Establecemos los atributos
		ariadnaCommandModel.setCommand(commandReceived.getCommand());
		ariadnaCommandModel.setConversationId(commandReceived.getConversation().getConversationId());
		ariadnaCommandModel.setLanguage(commandReceived.getUser().getLocale().substring(0, 2));
		
		// Ejecutamos el comando
		CommandResponseModel response = commandServiceImpl.sendCommand(ariadnaCommandModel);
		
		//Creamos una respuesta para google assistant a partir de la respuesta de bot Ariadna
		GoogleAssistantResponseModel googleAssistantResponse = new GoogleAssistantResponseModel();
		
		//Establecemos la respuesta
		googleAssistantResponse.setExpectUserResponse(true);
		googleAssistantResponse.setConversationToken("");
		
		ExpectedInputs expectedInput = new ExpectedInputs();
		InputPrompt inputPrompt = new InputPrompt();
		RichInitialPrompt richInitialPrompt = new RichInitialPrompt();
		Items items = new Items();
		SimpleResponse simpleResponse = new SimpleResponse();
		simpleResponse.setDisplayText(response.getCommand());
		simpleResponse.setTextToSpeech(response.getCommand());
		items.setSimpleResponse(simpleResponse);
		richInitialPrompt.setSuggestions(new String[0]);
		Items[] itemsArr = new Items[1];
		itemsArr[0] = items;
		richInitialPrompt.setItems(itemsArr);
		inputPrompt.setRichInitialPrompt(richInitialPrompt);
		expectedInput.setInputPrompt(inputPrompt);
		ExpectedInputs[] expectedInputArr = new ExpectedInputs[1];
		expectedInputArr[0] = expectedInput;
		googleAssistantResponse.setExpectedInputs(expectedInputArr);
		
		//Devolvemos la respuesta
		return ResponseEntity.ok().body(json.toJson(googleAssistantResponse));
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
		
		//Comprovamos si hay que generar un conversation ID
		if(commandReceived.getConversationId() == null || commandReceived.getConversationId() == "") {
			commandReceived.setConversationId(commandServiceImpl.getNewConversationId());
		}
		
		// Ejecutamos el comando
		CommandResponseModel response = commandServiceImpl.sendCommand(commandReceived);

		//Devolvemos la respuesta del bot
		return ResponseEntity.ok().body(json.toJson(response));
	}

}
