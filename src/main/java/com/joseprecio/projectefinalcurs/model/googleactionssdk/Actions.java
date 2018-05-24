package com.joseprecio.projectefinalcurs.model.googleactionssdk;

public class Actions
{
    private String description;

    private Fulfillment fulfillment;

    private String name;

    private Intent intent;

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public Fulfillment getFulfillment ()
    {
        return fulfillment;
    }

    public void setFulfillment (Fulfillment fulfillment)
    {
        this.fulfillment = fulfillment;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Intent getIntent ()
    {
        return intent;
    }

    public void setIntent (Intent intent)
    {
        this.intent = intent;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [description = "+description+", fulfillment = "+fulfillment+", name = "+name+", intent = "+intent+"]";
    }
}