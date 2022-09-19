package com.example.client;

import com.example.database.UserRep;
import com.example.entities.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{
    @FXML
    private Button b_login;
    @FXML
    private TextField tf_login;
    @FXML
    private PasswordField pf_haslo;
    @FXML
    private TextField tf_loginR;
    @FXML
    private PasswordField pf_haslo1R;
    @FXML
    private PasswordField pf_haslo2R;
    @FXML
    private Button b_registerR;
    @FXML
    private Label l_infoL;
    @FXML
    private Label l_infoR;

    @FXML
    private Label l_close;
    @FXML
    private Label l_minimalize;


    private Stage stage;
    private Scene scene;
    private Parent root;

    private static String login;

    private Socket socket;
    private static Client client;

    private UserRep ur = new UserRep();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        b_login.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                try
                {
                    boolean exists = false;


                    MainClient.setName(tf_login.getText());


                    for (User user : ur.findAll())
                    {
                        if (user.getLogin().equals(tf_login.getText()))// login sie zgadza
                        {
                            exists = true;
                            //System.out.println(user.getLogin());

                            if (pf_haslo.getText().equals(user.getHaslo()))
                            {
                                MainClient.setName(tf_login.getText());

                                FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("/com/example/hello-view.fxml"));
                                root = fxmlLoader.load();

                                stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                                scene = new Scene(root);
                                scene.getStylesheets().add("style.css");
                                stage.setScene(scene);

                                stage.show();
                            }
                            else
                            {
                                l_infoL.setText("Nieprawidłowe hasło");
                            }
                        }
                    }
                    if(exists == false)
                    {
                        System.out.println("ten nieprawidlowy login "+tf_login.getText());
                        l_infoL.setText("Nieprawidłowe login");
                    }

                } catch (Exception e)
                {
                    System.out.println(e);
                    e.printStackTrace();
                }

            }
        });

        b_registerR.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                User u = new User();
                boolean exists = false;

                for (User user : ur.findAll())
                {
                    if (user.getLogin().equals(tf_loginR.getText()))
                    {
                        exists = true;
                        l_infoR.setText("Użytkownik o podanym loginie już istnieje, nie można się pod niego podszywać :((");
                        break;
                    }
                }

                if (exists == false)
                {
                    u.setLogin(tf_loginR.getText());

                    if (pf_haslo1R.getText().equals(pf_haslo2R.getText()))
                    {
                        u.setId(ur.findLastId());
                        u.setHaslo(pf_haslo2R.getText());
                        ur.addUser(u);
                        l_infoR.setText("Możesz się już zalogować");
                    } else
                    {
                        l_infoR.setText("Podane hasła nie są identyczne, kup se okulary, to się tam nie zmieści, tam są same kropki, tego nie zobaczy, heee heee heee ahhha");

                    }
                }
            }
        });

        l_close.setOnMouseEntered(e -> l_close.setStyle("-fx-text-fill: #FFC98B;" + "-fx-cursor: hand;"));
        l_close.setOnMouseExited(e -> l_close.setStyle("-fx-text-fill: black;"));

        l_minimalize.setOnMouseEntered(e -> l_minimalize.setStyle("-fx-text-fill: #FFC98B;"+ "-fx-cursor: hand;"));
        l_minimalize.setOnMouseExited(e -> l_minimalize.setStyle("-fx-text-fill: black;"));

    }


    double x, y;
    @FXML
    void draged (MouseEvent event)
    {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void pressed (MouseEvent event)
    {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    void minimalize (MouseEvent event)
    {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void close (MouseEvent event)
    {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    public static Client getClient()
    {
        return client;
    }

    public static void setClient(Client client)
    {
        LoginController.client = client;
    }

}


