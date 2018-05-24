package com.joseprecio.projectefinalcurs.model;

import java.util.ArrayList;

import org.hibernate.validator.constraints.NotEmpty;

public class LanguagesForm {

	@NotEmpty
	private ArrayList<String> languages;

	public ArrayList<String> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}
	
}
