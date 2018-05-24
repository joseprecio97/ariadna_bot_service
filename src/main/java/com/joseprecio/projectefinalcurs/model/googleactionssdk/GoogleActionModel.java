package com.joseprecio.projectefinalcurs.model.googleactionssdk;

public class GoogleActionModel
{
    private String locale;

    private Conversations conversations;

    private Actions[] actions;

    public String getLocale ()
    {
        return locale;
    }

    public void setLocale (String locale)
    {
        this.locale = locale;
    }

    public Conversations getConversations ()
    {
        return conversations;
    }

    public void setConversations (Conversations conversations)
    {
        this.conversations = conversations;
    }

    public Actions[] getActions ()
    {
        return actions;
    }

    public void setActions (Actions[] actions)
    {
        this.actions = actions;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [locale = "+locale+", conversations = "+conversations+", actions = "+actions+"]";
    }
}