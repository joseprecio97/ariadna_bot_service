package com.joseprecio.projectefinalcurs.model;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class IntentForm {

	@NotEmpty
	private String id;
	@NotEmpty
	private List<String> languages;

	public IntentForm() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

}
