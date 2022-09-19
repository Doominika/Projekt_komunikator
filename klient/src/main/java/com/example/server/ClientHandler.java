package com.example.server;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable
{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private String user2;


    public ClientHandler(Socket socket)
    {
        try
        {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.username = bufferedReader.readLine();
            this.user2 = bufferedReader.readLine();

            System.out.println(username + " and " + user2);

            boolean exists = false;

            for (ClientHandler clientHandler : clientHandlers)
            {
               if(clientHandler.user2.equals(this.user2) && clientHandler.username.equals(this.username))
               {
                   exists = true;
                   break;
               }
            }
            if(exists == false)
            {
                clientHandlers.add(this);
            }
        }
        catch (Exception e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run()
    {
        String messageFromClient;

        while(socket.isConnected())
        {
            try
            {
                messageFromClient = bufferedReader.readLine();

                JSONObject obj = new JSONObject(messageFromClient);
                String typ = obj.optString(("typ"));

                {
                    String msg = obj.optString(("tresc"));
                    String odbiorca = obj.optString("odbiorca");
                    broadcastMessage(msg, odbiorca);
                }
            }
            catch (Exception e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
        System.out.println("rozłączono z serwerem");
    }

    public void broadcastMessage(String messageToSend, String odbiorca)
    {
        for (ClientHandler clientHandler : clientHandlers)
        {
            try
            {
                if(clientHandler.username.equals(odbiorca) && clientHandler.user2.equals(username))
                {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }
            catch (Exception e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler()
    {
        clientHandlers.remove(this);
        System.out.println(username + " gość wyszedł");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        removeClientHandler();
        try
        {
            if(bufferedReader != null)
            {
                bufferedReader.close();
            }
            if(bufferedWriter != null)
            {
                bufferedWriter.close();
            }
            if(socket != null)
            {
                socket.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("Mamy problem!");
        }
    }


}
