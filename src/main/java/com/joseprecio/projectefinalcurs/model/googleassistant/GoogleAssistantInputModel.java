package com.joseprecio.projectefinalcurs.model.googleassistant;

import java.util.ArrayList;

public class GoogleAssistantInputModel {

	private String intent;
	private ArrayList<GoogleAssistantRawInputModel> rawInputs;

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public ArrayList<GoogleAssistantRawInputModel> getRawInputs() {
		return rawInputs;
	}

	public void setRawInputs(ArrayList<GoogleAssistantRawInputModel> rawInputs) {
		this.rawInputs = rawInputs;
	}

}
