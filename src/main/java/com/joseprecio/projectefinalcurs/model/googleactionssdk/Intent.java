package com.joseprecio.projectefinalcurs.model.googleactionssdk;

public class Intent
{
    private Trigger trigger;

    private String name;

    public Trigger getTrigger ()
    {
        return trigger;
    }

    public void setTrigger (Trigger trigger)
    {
        this.trigger = trigger;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [trigger = "+trigger+", name = "+name+"]";
    }
}
