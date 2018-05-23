
package com.joseprecio.projectefinalcurs.model.googleassistant;

import java.util.ArrayList;

public class GoogleAssistantCommandModel {

	private GoogleAssistantUserModel user;
    private GoogleAssistantConversationModel conversation;
    private ArrayList<GoogleAssistantInputModel> inputs;
    
    public String getCommand() {
    	int inputsSize = inputs.size() - 1;
    	int rawsInput = inputs.get(inputsSize).getRawInputs().size() - 1;
    	
    	return inputs.get(inputsSize).getRawInputs().get(rawsInput).getQuery();
    }
    
	public ArrayList<GoogleAssistantInputModel> getInputs() {
		return inputs;
	}

	public void setInputs(ArrayList<GoogleAssistantInputModel> inputs) {
		this.inputs = inputs;
	}

	public GoogleAssistantUserModel getUser() {
		return user;
	}

	public void setUser(GoogleAssistantUserModel user) {
		this.user = user;
	}

	public GoogleAssistantConversationModel getConversation() {
		return conversation;
	}

	public void setConversation(GoogleAssistantConversationModel conversation) {
		this.conversation = conversation;
	}

}
