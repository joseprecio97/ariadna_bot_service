package com.joseprecio.projectefinalcurs.bot;

import java.util.HashMap;

public class ScriptResponse {

	boolean valid;
	HashMap<String, String> prompts;
	Conversation conversation;
	
	public ScriptResponse() {
		valid = true;
		prompts = new HashMap<String, String>();
		
		//Inicializamos el HashMap de prompts
		for(String language : BotConstants.BOT_AVAILABLE_LANGUAGE) {
			prompts.put(language, null);
		}
	}
	
	public Conversation getConversation() {
		return conversation;
	}
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public HashMap<String, String> getPrompts() {
		return prompts;
	}
	public void setPrompts(HashMap<String, String> prompts) {
		this.prompts = prompts;
	}
	
}
