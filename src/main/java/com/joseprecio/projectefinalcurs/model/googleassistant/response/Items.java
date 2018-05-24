package com.joseprecio.projectefinalcurs.model.googleassistant.response;

public class Items
{
    private SimpleResponse simpleResponse;

    public SimpleResponse getSimpleResponse ()
    {
        return simpleResponse;
    }

    public void setSimpleResponse (SimpleResponse simpleResponse)
    {
        this.simpleResponse = simpleResponse;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [simpleResponse = "+simpleResponse+"]";
    }
}