package com.files4Dayz.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController extends Application {

    private String ip;
    private int port;

    LoginController(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {
        final ClientController client = new ClientController(ip, port);
        final String realUser = client.getUserName();
        final String realPass = client.getUserPass();
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("Login");
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);

        final Label warningLabel = (Label) loader.getNamespace().get("warningLabel");
        warningLabel.setVisible(false);

        final Button loginButton = (Button) loader.getNamespace().get("loginButton");
        final Button cancelButton = (Button) loader.getNamespace().get("cancelButton");
        loginButton.setOnAction((event) -> {
            final String username = ((TextField) loader.getNamespace().get("usernameField")).getText();
            final String password = ((TextField) loader.getNamespace().get("passwordField")).getText();
            try {
                if (client.getClient().sendCredentials(username,password)) {
                    client.start(new Stage());
                    primaryStage.close();
                } else {
                    warningLabel.setVisible(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction((event -> primaryStage.close()));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
