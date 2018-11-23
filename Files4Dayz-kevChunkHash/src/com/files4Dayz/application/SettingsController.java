package com.files4Dayz.application;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class SettingsController extends Application {

    @FXML
    private TextField pathField;

    public SettingsController() {

    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        start(primaryStage,null);
    }

    public void start(Stage primaryStage, final ServerController server) throws IOException {
        primaryStage.setTitle("Settings");
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);

        pathField = (TextField) loader.getNamespace().get("pathField");
        if (server.getDownloadPath() != null) {
            pathField.setText(server.getDownloadPath().getAbsolutePath());
        }

        final Button selectPath = (Button) loader.getNamespace().get("selectPath");
        selectPath.setOnAction((event) -> {
            choosePath(primaryStage, server);
        });

        final Button okButton = (Button) loader.getNamespace().get("okButton");
        final Button cancelButton = (Button) loader.getNamespace().get("cancelButton");
        okButton.setOnAction((event) -> {
            server.setDownloadPath(new File(pathField.getText()));
            primaryStage.close();
        });
        cancelButton.setOnAction((event) -> {
            primaryStage.close();
        });


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void choosePath(Stage primaryStage, ServerController server) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        final File dir = dirChooser.showDialog(primaryStage);
        if (dir != null) {
            pathField.setText(dir.getAbsolutePath());
        }
    }
}
