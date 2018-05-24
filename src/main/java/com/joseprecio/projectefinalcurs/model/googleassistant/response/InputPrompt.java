package com.joseprecio.projectefinalcurs.model.googleassistant.response;

public class InputPrompt
{
    private RichInitialPrompt richInitialPrompt;

    public RichInitialPrompt getRichInitialPrompt ()
    {
        return richInitialPrompt;
    }

    public void setRichInitialPrompt (RichInitialPrompt richInitialPrompt)
    {
        this.richInitialPrompt = richInitialPrompt;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [richInitialPrompt = "+richInitialPrompt+"]";
    }
}
			
			