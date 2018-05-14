
package com.joseprecio.projectefinalcurs.model.googleassistant;

public class QueryResult {

    private String queryText;
    private String languageCode;
    
    public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }


}
