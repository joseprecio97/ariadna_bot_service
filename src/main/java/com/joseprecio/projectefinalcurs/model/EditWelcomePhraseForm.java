package com.joseprecio.projectefinalcurs.model;

import org.hibernate.validator.constraints.NotEmpty;

public class EditWelcomePhraseForm extends WelcomePhraseForm {

	@NotEmpty
	private String oldPhrase;

	public String getOldPhrase() {
		return oldPhrase;
	}

	public void setOldPhrase(String oldPhrase) {
		this.oldPhrase = oldPhrase;
	}

}
