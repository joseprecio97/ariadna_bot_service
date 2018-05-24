package com.joseprecio.projectefinalcurs.model;

import org.hibernate.validator.constraints.NotEmpty;

public class WelcomePhraseForm {

	@NotEmpty
	private String language;
	@NotEmpty
	private String phrase;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

}
