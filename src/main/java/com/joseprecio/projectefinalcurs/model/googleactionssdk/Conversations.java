package com.joseprecio.projectefinalcurs.model.googleactionssdk;

public class Conversations
{
    private AriadnaBotService ariadnaBotService;

    public AriadnaBotService getAriadnaBotService ()
    {
        return ariadnaBotService;
    }

    public void setAriadnaBotService (AriadnaBotService ariadnaBotService)
    {
        this.ariadnaBotService = ariadnaBotService;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ariadna-bot-service = "+ariadnaBotService+"]";
    }
}
