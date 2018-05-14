
package com.joseprecio.projectefinalcurs.model.googleassistant;

public class GoogleAssistantCommandModel {

    private QueryResult queryResult;
    private String session;
    
    public GoogleAssistantCommandModel() {
    	
    }
    
    public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public QueryResult getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(QueryResult queryResult) {
        this.queryResult = queryResult;
    }

}
