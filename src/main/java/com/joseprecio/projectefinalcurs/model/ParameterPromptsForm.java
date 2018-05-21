package com.joseprecio.projectefinalcurs.model;

import org.hibernate.validator.constraints.NotEmpty;

public class ParameterPromptsForm {

	@NotEmpty
	private String id;
	@NotEmpty
	private String language;
	@NotEmpty
	private String parametername;
	private String promptparameter;
	private String promptbadvalue;
	private String promptinvalid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getParametername() {
		return parametername;
	}

	public void setParametername(String parametername) {
		this.parametername = parametername;
	}

	public String getPromptparameter() {
		return promptparameter;
	}

	public void setPromptparameter(String promptparameter) {
		this.promptparameter = promptparameter;
	}

	public String getPromptbadvalue() {
		return promptbadvalue;
	}

	public void setPromptbadvalue(String promptbadvalue) {
		this.promptbadvalue = promptbadvalue;
	}

	public String getPromptinvalid() {
		return promptinvalid;
	}

	public void setPromptinvalid(String promptinvalid) {
		this.promptinvalid = promptinvalid;
	}

}
