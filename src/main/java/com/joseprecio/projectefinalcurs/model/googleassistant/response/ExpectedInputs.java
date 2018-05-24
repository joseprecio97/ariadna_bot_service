package com.joseprecio.projectefinalcurs.model.googleassistant.response;

public class ExpectedInputs
{
    private PossibleIntents[] possibleIntents;

    private InputPrompt inputPrompt;

    public PossibleIntents[] getPossibleIntents ()
    {
        return possibleIntents;
    }

    public void setPossibleIntents (PossibleIntents[] possibleIntents)
    {
        this.possibleIntents = possibleIntents;
    }

    public InputPrompt getInputPrompt ()
    {
        return inputPrompt;
    }

    public void setInputPrompt (InputPrompt inputPrompt)
    {
        this.inputPrompt = inputPrompt;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [possibleIntents = "+possibleIntents+", inputPrompt = "+inputPrompt+"]";
    }
}