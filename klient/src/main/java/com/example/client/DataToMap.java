package com.example.client;

import javafx.scene.layout.VBox;

public class DataToMap
{
    Client client;
    VBox vBox;
    String receiverName;


    public DataToMap(Client client, VBox vBox, String receiverName)
    {
        this.client = client;
        this.vBox = vBox;
        this.receiverName = receiverName;
    }

    public DataToMap()
    {
    }


    public String getReceiverName()
    {
        return receiverName;
    }

    public void setReceiverName(String receiverName)
    {
        this.receiverName = receiverName;
    }

    public Client getClient()
    {
        return client;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    public VBox getvBox()
    {
        return vBox;
    }

    public void setvBox(VBox vBox)
    {
        this.vBox = vBox;
    }
}
