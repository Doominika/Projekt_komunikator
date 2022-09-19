package com.example.client;

import com.example.database.JDBCFunctions;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client
{
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    private String user2;

    public Client(Socket socket, String username)
    {
        try
        {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;

        }
        catch(Exception e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(String messageToSend)
    {
        try
        {
            //czytac z bazy, dodac nowa wiadomosc i wyslac z powrotem do bazy
            //pobrac na poczatku a na koniec sesji oddac - tylko serwer
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        }
        catch(Exception e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage(VBox vBox)
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
                        ViewController.addLabel(msgFromGroupChat, vBox);

                        try
                        {
                            JDBCFunctions jdbc = new JDBCFunctions();
                            jdbc.add(user2, msgFromGroupChat, ViewController.getNameOfConversation());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        System.out.println(user2 + " -> "+username +" : " + msgFromGroupChat);
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

    public void closeEverything()
    {
        try
        {
            if (this.bufferedReader != null)
            {
                this.bufferedReader.close();
            }
            if (this.bufferedWriter != null)
            {
                this.bufferedWriter.close();
            }
            if (this.socket != null)
            {
                this.socket.close();
            }
        } catch (Exception e)
        {
            System.out.println("Mamy problem!");
        }
    }

    public String getUser2()
    {
        return user2;
    }

    public void setUser2(String user2)
    {
        this.user2 = user2;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public BufferedWriter getBufferedWriter()
    {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter)
    {
        this.bufferedWriter = bufferedWriter;
    }

    public BufferedReader getBufferedReader()
    {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader)
    {
        this.bufferedReader = bufferedReader;
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