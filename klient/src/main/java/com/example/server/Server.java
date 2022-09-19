package com.example.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private ServerSocket serverSocket;


    public Server(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args)
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(8080);
            Server server = new Server(serverSocket);
            server.startServer();
        }
        catch (Exception e)
        {
            System.out.println(e.toString());

        }
    }

    public void startServer()
    {
        try
        {
            while(!serverSocket.isClosed())
            {
                Socket socket = serverSocket.accept();

                ClientHandler client = new ClientHandler(socket);
                System.out.println("klient hendler ");

                Thread thread = new Thread(client);
                thread.start();
            }
        }
        catch (Exception e)
        {
            System.out.println("Mamy problem");
            closeServerSocket();
        }
    }

    public void closeServerSocket()
    {
        try
        {
            if(serverSocket != null)
            {
                serverSocket.close();
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }

}
