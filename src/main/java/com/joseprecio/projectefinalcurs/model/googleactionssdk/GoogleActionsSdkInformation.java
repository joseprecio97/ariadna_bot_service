package com.joseprecio.projectefinalcurs.model.googleactionssdk;

import java.util.ArrayList;
import java.util.HashMap;

public class GoogleActionsSdkInformation {

	private String projectId;
	private ArrayList<String> languages;
	private HashMap<String, ArrayList<String>> welcomePhrases;

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

	public HashMap<String, ArrayList<String>> getWelcomePhrases() {
		return welcomePhrases;
	}

	public void setWelcomePhrases(HashMap<String, ArrayList<String>> welcomePhrases) {
		this.welcomePhrases = welcomePhrases;
	}

}
