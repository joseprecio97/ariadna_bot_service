package com.joseprecio.projectefinalcurs.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Intent del Bot
 * 
 * @author joseprecio
 *
 */

public class Intent {

	private String id;
	private List<String> languages;
	private LinkedHashMap<String, Parameter> parameters;
	
	public Intent() {
		this.languages = new ArrayList<String>();
		this.parameters = new LinkedHashMap<String, Parameter>();
	}
	
	public List<String> addLanguage(String language){
		this.languages.add(language);
		
		return this.languages;
	}
	
	public List<String> getLanguages() {
		return languages;
	}

	public void addParameter(String name, Parameter parameter) {
		parameters.put(name, parameter);
	}
	
	public HashMap<String, Parameter> getParameters(){
		return parameters;
	}

	public void setParameters(LinkedHashMap<String, Parameter> parameters) {
		this.parameters = parameters;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
