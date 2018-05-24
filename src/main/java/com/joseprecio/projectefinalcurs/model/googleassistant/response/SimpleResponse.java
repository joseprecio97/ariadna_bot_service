package com.joseprecio.projectefinalcurs.model.googleassistant.response;

public class SimpleResponse
{
    private String displayText;

    private String textToSpeech;

    public String getDisplayText ()
    {
        return displayText;
    }

    public void setDisplayText (String displayText)
    {
        this.displayText = displayText;
    }

    public String getTextToSpeech ()
    {
        return textToSpeech;
    }

    public void setTextToSpeech (String textToSpeech)
    {
        this.textToSpeech = textToSpeech;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [displayText = "+displayText+", textToSpeech = "+textToSpeech+"]";
    }
}