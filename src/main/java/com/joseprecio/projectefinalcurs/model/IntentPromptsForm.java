package com.joseprecio.projectefinalcurs.model;

import org.hibernate.validator.constraints.NotEmpty;

public class IntentPromptsForm {

	@NotEmpty
	private String language;
	@NotEmpty
	private String id;
	private String prompterror;
	private String promptnewconversationerror;
	private String promptendconversationerror;
	private String promptfinalmessage;
	private String promptcancel;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrompterror() {
		return prompterror;
	}

	public void setPrompterror(String prompterror) {
		this.prompterror = prompterror;
	}

	public String getPromptnewconversationerror() {
		return promptnewconversationerror;
	}

	public void setPromptnewconversationerror(String promptnewconversationerror) {
		this.promptnewconversationerror = promptnewconversationerror;
	}

	public String getPromptendconversationerror() {
		return promptendconversationerror;
	}

	public void setPromptendconversationerror(String promptendconversationerror) {
		this.promptendconversationerror = promptendconversationerror;
	}

	public String getPromptfinalmessage() {
		return promptfinalmessage;
	}

	public void setPromptfinalmessage(String promptfinalmessage) {
		this.promptfinalmessage = promptfinalmessage;
	}

	public String getPromptcancel() {
		return promptcancel;
	}

	public void setPromptcancel(String promptcancel) {
		this.promptcancel = promptcancel;
	}

}
