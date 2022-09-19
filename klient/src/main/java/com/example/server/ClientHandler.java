package com.example.server;

import com.example.client.MainClient;
import com.example.database.ConversationRep;
import com.example.database.JDBCFunctions;
import com.example.database.UserRep;
import com.example.entities.Conversation;
import com.example.entities.User;
import com.example.other.DataMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
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
                //String msg = obj.optString(("tresc"));
                //String odbiorca = obj.optString("odbiorca");

             /*   if(typ.equals("logowanie"))
                {
                    UserRep ur = new UserRep();

                    String login = obj.optString(("login"));
                    String haslo = obj.optString(("haslo"));

                    boolean exists = false;

                    for (User user : ur.findAll())
                    {
                        if (user.getLogin().equals(login))// login sie zgadza
                        {
                            exists = true;
                            System.out.println(user.getLogin());

                            if (haslo.equals(user.getHaslo()))
                            {
                                JSONObject jo = new JSONObject();
                                jo.put("odp", "true");
                                broadcastMessage(jo.toString(), login);
                                break;
                            }
                        }
                    }
                    if(exists == false)
                    {
                        JSONObject jo = new JSONObject();
                        jo.put("odp", "false");
                        broadcastMessage(jo.toString(), login);
                    }
                }
                else*/
                {
                    String msg = obj.optString(("tresc"));
                    String odbiorca = obj.optString("odbiorca");
                    broadcastMessage(msg, odbiorca);
                }
/*
                if(typ.equals("pobierzhistorie"))
                {

                    Conversation conversation = new Conversation();
                    UserRep ur = new UserRep();
                    ConversationRep cr = new ConversationRep();

                    User u1 = ur.findByName(username);
                    User u2 = ur.findByName(odbiorca);

                    int w, m;

                    if (u1.getId()> u2.getId())
                    {
                        w = u1.getId();
                        m = u2.getId();
                        conversation.setUser1(u2);
                        conversation.setUser2(u1);
                    }
                    else
                    {
                        m = u1.getId();
                        w = u2.getId();
                        conversation.setUser1(u1);
                        conversation.setUser2(u2);
                    }

                    Integer idOfConversation = cr.ifConversationExists(m,w);

                    if (idOfConversation == 0)
                    {
                        conversation.setId(cr.findLastId());
                        cr.addConversation(conversation);

                        try
                        {
                            JDBCFunctions jdbc = new JDBCFunctions();
                            Integer idofconv = conversation.getId();
                            jdbc.createTable(idofconv.toString());  //???
                        } catch (Exception e)
                        {
                            System.out.println(e.toString());
                            e.printStackTrace();
                        }

                    }
                    else    //konwersacja istnieje w bazie
                    {
                        try
                        {

                            JDBCFunctions jdbc = new JDBCFunctions();
                            JSONObject data = new JSONObject();
                            JSONArray tablica = new JSONArray();

                            for (DataMessage dm : jdbc.getAllMessages(idOfConversation.toString()))
                            {
                                data.put("nadawca", dm.getSender());
                                data.put("wiadomosc", dm.getMessage());

                                tablica.put(data);
                            }

                            broadcastMessage(tablica.toString(), username);

                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
*/

               // broadcastMessage(msg, odbiorca);
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

    public void removeClientHandler()   // info przy nickname - nieaktywny, a przy wejściu na serwer - aktywny
    {
        //broadcastMessage(username + " odszedł", user2);
        clientHandlers.remove(this);
        //broadcastMessage(username + " odszedł", user2);
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
