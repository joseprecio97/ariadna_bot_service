package com.joseprecio.projectefinalcurs.model.googleassistant.response;

public class GoogleAssistantResponseModel
{
    private String conversationToken;

    private ExpectedInputs[] expectedInputs;

    private boolean expectUserResponse;

    public String getConversationToken ()
    {
        return conversationToken;
    }

    public void setConversationToken (String conversationToken)
    {
        this.conversationToken = conversationToken;
    }

    public ExpectedInputs[] getExpectedInputs ()
    {
        return expectedInputs;
    }

    public void setExpectedInputs (ExpectedInputs[] expectedInputs)
    {
        this.expectedInputs = expectedInputs;
    }

    public boolean isExpectUserResponse ()
    {
        return expectUserResponse;
    }

    public void setExpectUserResponse (boolean expectUserResponse)
    {
        this.expectUserResponse = expectUserResponse;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [conversationToken = "+conversationToken+", expectedInputs = "+expectedInputs+", expectUserResponse = "+expectUserResponse+"]";
    }
}