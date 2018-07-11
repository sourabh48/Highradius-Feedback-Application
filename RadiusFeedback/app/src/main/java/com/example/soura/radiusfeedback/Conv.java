package com.example.soura.radiusfeedback;

public class Conv
{
    public long timestamp;

    public Conv(){}


    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public Conv(boolean seen, long timestamp)
    {
        this.timestamp = timestamp;
    }
}