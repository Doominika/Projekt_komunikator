package com.example.other;

import java.util.Date;

public class DataMessage
{
    private String Sender;
    private String message;

    public DataMessage(String sender, String message)
    {
        Sender = sender;
        this.message = message;
    }

    public DataMessage()
    {
    }

    public String getSender()
    {
        return Sender;
    }

    public void setSender(String sender)
    {
        this.Sender = sender;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "DataMessage{" +
                "Sender='" + Sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
