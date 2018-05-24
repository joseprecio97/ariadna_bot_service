package com.joseprecio.projectefinalcurs.model.googleassistant.response;

public class RichInitialPrompt
{
    private Items[] items;

    private String[] suggestions;

    public Items[] getItems ()
    {
        return items;
    }

    public void setItems (Items[] items)
    {
        this.items = items;
    }

    public String[] getSuggestions ()
    {
        return suggestions;
    }

    public void setSuggestions (String[] suggestions)
    {
        this.suggestions = suggestions;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [items = "+items+", suggestions = "+suggestions+"]";
    }
}