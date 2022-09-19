package com.example.client;

import com.example.database.JDBCFunctions;
import com.example.other.DataMessage;
import com.example.server.ClientHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.JSONObject;
import com.example.database.ConversationRep;
import com.example.database.UserRep;
import com.example.entities.Conversation;
import com.example.entities.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ViewController implements Initializable
{
    @FXML
    private Button b_send;
    @FXML
    private TextField tf_message;
    @FXML
    private ScrollPane sp_window;
    @FXML
    private ListView lv_users;
    @FXML
    private Button b_search;
    @FXML
    private TextField tf_search;
    @FXML
    private Label l_close;
    @FXML
    private Label l_minimalize;


    private String actReceiver;
    private static String nameOfConversation;



    private Socket socket;
    private Client client;

    private Stage stage;


    private UserRep ur = new UserRep();
    private ConversationRep cr = new ConversationRep();


    ObservableList<String> listOfUsers = FXCollections.observableArrayList();   // lista znajomych
    List<DataToMap> listOfConnections = new ArrayList<>();  //lista połączeń w danej sesji

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        updateListofUsers();

        lv_users.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                if (mouseEvent.getClickCount() == 2)
                {
                    boolean exists = false;

                    String receiver = lv_users.getSelectionModel().getSelectedItem().toString();
                    actReceiver = receiver;

                    for (DataToMap c : listOfConnections)
                    {
                        if (c.getReceiverName().equals(receiver))    //istnieje polaczenie z tym userem w danej sesji, jest na naszej liscie listOfUsers
                        {
                            System.out.println("mielismy juz polaczenie");
                            sp_window.setContent((VBox) c.getvBox());
                            client = c.getClient();
                            exists = true;
                            break;
                        }
                    }

                    if (exists == false)    //nie było jeszcze polaczenia w danej sesji z tym userem, dodajemy do listy listOfUsers
                    {
                        //tworzymy nowe polaczenie z serwerem
                        System.out.println("nowe polaczenie");
                        try
                        {
                            //socket = new Socket("localhost", 8080); ////
                            socket = new Socket("192.168.0.5", 8080); ////

                            client = new Client(socket, MainClient.getName());
                            client.setUser2(actReceiver);

                            client.sendMessage(MainClient.getName());
                            client.sendMessage(actReceiver);

                            DataToMap dtm = new DataToMap();
                            dtm.setClient(client);
                            dtm.setvBox(new VBox());
                            dtm.setReceiverName(actReceiver);

                            createVBox(dtm);

                            listOfConnections.add(dtm);

                            System.out.println("Połączono z serwerem...");

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                        Conversation conversation = new Conversation();

                        User u1 = ur.findByName(MainClient.getName());
                        User u2 = ur.findByName(receiver);

                        int w, m;

                        if (u1.getId() > u2.getId())
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

                        Integer idOfConversation = cr.ifConversationExists(m, w);
                        if (idOfConversation == 0) //nie ma takiej konwersacji w bazie
                        {
                            conversation.setId(cr.findLastId());
                            nameOfConversation = String.valueOf(cr.findLastId());
                            cr.addConversation(conversation);

                            try
                            {
                                JDBCFunctions jdbc = new JDBCFunctions();
                                Integer idofconv = conversation.getId();
                                jdbc.createTable(idofconv.toString());
                            }
                            catch (Exception e)
                            {
                                System.out.println(e.toString());
                                e.printStackTrace();
                            }

                        }
                        else    //konwersacja istnieje w bazie
                        {
                            try
                            {
                                // pobieramy historie rozmow z bazy

                                JDBCFunctions jdbc = new JDBCFunctions();
                                List<DataMessage> list = jdbc.getAllMessages(idOfConversation.toString());


                                for (DataMessage dm : list)
                                {

                                    //System.out.println(actReceiver);

                                    if (dm.getSender().equals(MainClient.getName()))
                                    {
                                        addLabelOfMyMessage(dm.getMessage(), (VBox) sp_window.getContent());
                                    }
                                    else if(dm.getSender().equals(actReceiver))
                                    {

                                        HBox hBox = new HBox();
                                        hBox.setAlignment(Pos.CENTER_LEFT);
                                        hBox.setPadding(new Insets(5, 25, 5, 5));

                                        Text text = new Text(dm.getMessage());
                                        TextFlow textFlow = new TextFlow(text);

                                        textFlow.setStyle("-fx-background-color: rgba(254, 179, 132, 0.6);" + "-fx-background-radius: 5px;");

                                        textFlow.setPadding(new Insets(5, 10, 5, 10));
                                        text.setFill(Color.rgb(21, 37, 34));

                                        text.setStyle("-fx-font-size: 13px;");

                                        hBox.setPrefHeight(30);
                                        hBox.getChildren().add(textFlow);

                                        VBox vbox = (VBox) sp_window.getContent();
                                        vbox.getChildren().add(hBox);
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                System.out.println(e);
                                e.printStackTrace();
                            }

                            nameOfConversation = idOfConversation.toString();
                        }

                    }

                    client.listenForMessage((VBox) sp_window.getContent());



                }
            }
        });


        b_search.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                String username2 = tf_search.getText();
                boolean exists = false;
                boolean existsInList = false;

                for (User u : ur.findAll())
                {
                    if (username2.equals(u.getLogin()))
                    {
                        exists = true;

                        for (String s : listOfUsers)
                        {
                            if (s.equals(tf_search.getText()))
                            {
                                existsInList = true;

                            }
                        }

                        if (existsInList == false)
                        {
                            listOfUsers.add(tf_search.getText());
                            lv_users.setItems(listOfUsers);
                        } else
                        {
                            tf_search.setText("Już jest na liście");
                        }
                        break;
                    }
                }

                if (exists == false)
                {
                    ur.addUser(new User(ur.findLastId(), tf_search.getText(), ""));
                }
            }
        });


        b_send.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                String msgToSend = tf_message.getText();
                if (!msgToSend.isEmpty())
                {
                    addLabelOfMyMessage(msgToSend, (VBox) sp_window.getContent());

                    JSONObject odp = new JSONObject();
                    odp.put("typ", "wiadomosc");
                    odp.put("odbiorca", actReceiver);
                    odp.put("tresc", msgToSend);

                    client.sendMessage(odp.toString());

                    tf_message.clear();

                    try
                    {
                        JDBCFunctions jdbc = new JDBCFunctions();
                        jdbc.add(MainClient.getName(), msgToSend, nameOfConversation);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });


        l_close.setOnMouseEntered(e -> l_close.setStyle("-fx-text-fill: #FFC98B;" + "-fx-cursor: hand;"));
        l_close.setOnMouseExited(e -> l_close.setStyle("-fx-text-fill: black;"));

        l_minimalize.setOnMouseEntered(e -> l_minimalize.setStyle("-fx-text-fill: #FFC98B;" + "-fx-cursor: hand;"));
        l_minimalize.setOnMouseExited(e -> l_minimalize.setStyle("-fx-text-fill: black;"));

    }

    double x, y;

    @FXML
    void draged(MouseEvent event)
    {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void pressed(MouseEvent event)
    {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    void minimalize(MouseEvent event)
    {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void close(MouseEvent event)
    {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        for(DataToMap dtm : listOfConnections)
        {
            dtm.getClient().closeEverything();
        }
        MainClient.close();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


    public static void addLabel(String msgFromServer, VBox vbox)
    {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 25, 5, 5));

        Text text = new Text(msgFromServer);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgba(254, 179, 132, 0.6);" + "-fx-background-radius: 5px;");

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.rgb(21, 37, 34));

        text.setStyle("-fx-font-size: 13px;");

        hBox.setPrefHeight(30);
        hBox.getChildren().add(textFlow);

        // only contains static methods and one of them being run later
        // it will run provided runnable on the JavaFX application thread
        // ~ 25min
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                vbox.getChildren().add(hBox);
            }
        });
    }


    public static void addLabelOfMyMessage(String msgToSend, VBox vbox)
    {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 5, 5, 25));

        Text text = new Text(msgToSend);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgba(232, 152, 151, 0.6);" + "-fx-background-radius: 5px;");

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        //text.setFill(Color.rgb(224, 236, 222));
        text.setFill(Color.rgb(21, 37, 34));
        text.setStyle("-fx-font-size: 13px;");


        hBox.getChildren().add(textFlow);
        vbox.getChildren().add(hBox);

    }


    public void updateListofUsers()
    {
        listOfUsers.clear();
        String name = MainClient.getName();
        Integer id = ur.findByName(name).getId();

        for (Conversation i : cr.findFriends(id))
        {
            if (i.getUser1().getLogin().equals(name))
            {
                listOfUsers.add(i.getUser2().getLogin());
            } else
            {
                listOfUsers.add(i.getUser1().getLogin());
            }
        }
        lv_users.setItems(listOfUsers);
    }

    public void createVBox(DataToMap dtm)
    {
        VBox window = new VBox();
        window.setPrefWidth(655);
        window.setPrefHeight(466);
        window.setStyle("-fx-background-color: #4A6254;" +
                "-fx-background-radius: 2px;" +
                "-fx-text-fill: #152622;" +
                "-fx-border-radius: 4px;");


        window.heightProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldv, Number newv)
            {
                try
                {
                    sp_window.setVvalue((Double) newv);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


        dtm.setvBox(window);
        sp_window.setContent(window);
    }

    public static String getNameOfConversation()
    {
        return nameOfConversation;
    }

    public static void setNameOfConversation(String nameOfConversation)
    {
        ViewController.nameOfConversation = nameOfConversation;
    }

}

