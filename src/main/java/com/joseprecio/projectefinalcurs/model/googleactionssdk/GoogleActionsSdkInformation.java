package com.joseprecio.projectefinalcurs.model.googleactionssdk;

import java.util.ArrayList;

public class GoogleActionsSdkInformation {

	private String projectId;
	private ArrayList<String> languages;
	private String welcomeIntent;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public ArrayList<String> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}

	public String getWelcomeIntent() {
		return welcomeIntent;
	}

	public void setWelcomeIntent(String welcomeIntent) {
		this.welcomeIntent = welcomeIntent;
	}

}
