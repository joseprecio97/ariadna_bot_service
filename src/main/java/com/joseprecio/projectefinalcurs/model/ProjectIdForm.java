package com.joseprecio.projectefinalcurs.model;

import org.hibernate.validator.constraints.NotEmpty;

public class ProjectIdForm {

	@NotEmpty
	private String projectid;

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}
	
}
