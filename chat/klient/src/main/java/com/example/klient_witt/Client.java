package com.example.klient_witt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    @FXML
    private Button wyslij;
    @FXML
    private TextField pisz;
    @FXML
    private VBox historia;
    @FXML
    private ListView lista;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    private String user2;

    public Client(Socket socket, String username, String user2)
    {
        try
        {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
            this.user2 = user2;

        }
        catch(Exception e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage()
    {
        try
        {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.write(user2);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected())
            {
                String messageToSend = scanner.nextLine();

                historia.getChildren().add(new Label(messageToSend));

                bufferedWriter.write(username + ": " +messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
        catch(Exception e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage()
    {
         new Thread(new Runnable()
         {
             @Override
             public void run()
             {
                String msgFromGroupChat;

                while(socket.isConnected())
                {
                    try
                    {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                        historia.getChildren().add(new Label(msgFromGroupChat));
                    }
                    catch(Exception e)
                    {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }

                }
             }
         }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        try
        {
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (bufferedWriter != null)
            {
                bufferedWriter.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        } catch (Exception e)
        {
            System.out.println("Mamy problem!");
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Wpisz nick: ");
            String username = scanner.nextLine();
            System.out.println("Wpisz nick odbiorcy: ");
            String user2 = scanner.nextLine();

            Socket socket = new Socket("localhost", 8080);
            Client client = new Client(socket, username, user2);
            //zamienic miejscami ?
            client.listenForMessage();
            client.sendMessage();
        }
        catch(Exception e)
        {
            System.out.println("Problem");
        }


    }
}


/*

        try
        {

        }
        catch(Exception e)
        {

        }

 */