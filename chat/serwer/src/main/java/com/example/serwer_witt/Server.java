package com.example.serwer_witt;

import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public void startServer()
    {
        try
        {
            while(!serverSocket.isClosed())
            {
                Socket socket = serverSocket.accept();
                System.out.println("Nowy gość dołączył do czatu grupowego");

                ClientHandler client = new ClientHandler(socket);

                Thread thread = new Thread(client);
                thread.start();
            }
        }
        catch (Exception e)
        {
            System.out.println("MAmy problem");
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
        }
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

        }

    }
}
