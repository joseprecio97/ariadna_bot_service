package com.joseprecio.projectefinalcurs.model.googleactionssdk;

public class Trigger
{
    private String[] queryPatterns;

    public String[] getQueryPatterns ()
    {
        return queryPatterns;
    }

    public void setQueryPatterns (String[] queryPatterns)
    {
        this.queryPatterns = queryPatterns;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [queryPatterns = "+queryPatterns+"]";
    }
}