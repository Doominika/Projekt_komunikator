package com.example.serwer_witt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
            clientHandlers.add(this);
            broadcastMessage(username + " właśnie dołączył!");
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
                broadcastMessage(messageFromClient);
            }
            catch (Exception e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend)
    {
        for (ClientHandler clientHandler : clientHandlers)
        {
            try
            {
                if(clientHandler.username.equals(user2))
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

    public void removeClientHandler()   // info przy nickname - nieaktywny, a przy wejściu na serwer - aktywny
    {
        clientHandlers.remove(this);
        broadcastMessage(username + " odszedł");
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
