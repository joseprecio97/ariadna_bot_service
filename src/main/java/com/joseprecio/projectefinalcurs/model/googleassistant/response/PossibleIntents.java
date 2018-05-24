package com.joseprecio.projectefinalcurs.model.googleassistant.response;

public class PossibleIntents
{
    private String intent;

    public String getIntent ()
    {
        return intent;
    }

    public void setIntent (String intent)
    {
        this.intent = intent;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [intent = "+intent+"]";
    }
}
	