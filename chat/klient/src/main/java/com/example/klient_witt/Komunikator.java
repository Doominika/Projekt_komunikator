package com.example.klient_witt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Komunikator extends Application {

    public static void main(String[] args) {
        launch(args);
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

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxml = new FXMLLoader(Komunikator.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxml.load());

        stage.setScene(scene);
        stage.show();
    }
}
