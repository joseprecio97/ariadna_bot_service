package com.joseprecio.projectefinalcurs.model;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

public class GAUpdateModel {

	private String token;
	@Min(value=1)
	private int languages;
	@NotEmpty
	private String projectId;
	
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public int getLanguages() {
		return languages;
	}

	public void setLanguages(int languages) {
		this.languages = languages;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
