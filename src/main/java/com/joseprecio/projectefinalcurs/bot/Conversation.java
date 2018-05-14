package com.joseprecio.projectefinalcurs.bot;

import java.util.LinkedHashMap;

/**
 * Intent del Bot
 * 
 * @author joseprecio
 *
 */

public class Conversation {

	private boolean cancel;
	private String username;
	private String id;
	private String language;
	private LinkedHashMap<String, Parameter> parameters;
	private String nextCommingParameter;

	public Conversation() {
		this.parameters = new LinkedHashMap<String, Parameter>();
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNextCommingParameter() {
		return nextCommingParameter;
	}

	public void setNextCommingParameter(String nextCommingParameter) {
		this.nextCommingParameter = nextCommingParameter;
	}

	public String setLanguage(String language) {
		this.language = language;

		return this.language;
	}

	public String getLanguage() {
		return language;
	}

	public void addParameter(String name, Parameter parameter) {
		parameters.put(name, parameter);
	}

	public LinkedHashMap<String, Parameter> getParameters() {
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
