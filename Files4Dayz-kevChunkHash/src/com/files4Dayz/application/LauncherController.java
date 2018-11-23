package com.files4Dayz.application;

import com.files4Dayz.server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.files4Dayz.application.LauncherController.Selection.*;

public class LauncherController extends Application {

	private boolean loggedIn;

	@FXML
	private Button startButton;

	@FXML
	private RadioButton serverButton;

	@FXML
	private RadioButton clientButton;

	@FXML
	private TextField ip;
	@FXML
	private TextField port;

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	protected enum Selection {
		CLIENT,SERVER;
	}

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("LauncherApp.fxml"));
		final Parent root = loader.load();
		final Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

		//Establish nodes
		startButton = (Button) root.lookup("#startButton");
		startButton.setDisable(true);
		serverButton = (RadioButton) root.lookup("#serverButton");
		clientButton = (RadioButton) root.lookup("#clientButton");
		final ToggleGroup toggleGroup = new ToggleGroup();
		serverButton.setToggleGroup(toggleGroup);
		clientButton.setToggleGroup(toggleGroup);

		// Get radio button selection
		final Selection[] selection = new Selection[1];
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (((RadioButton)newValue.getToggleGroup().getSelectedToggle()).getText().equals("Client")) {
					selection[0] = CLIENT;
					if (!ipEmpty() && !portEmpty()) {
						startButton.setDisable(false);
					} else {
						startButton.setDisable(true);
					}
				} else {
					selection[0] = SERVER;
					if (!portEmpty()) {
						startButton.setDisable(false);
					} else {
						startButton.setDisable(true);
					}
				}
			}
		});

		ip = (TextField) root.lookup("#ip");
		port = (TextField) root.lookup("#port");
		// Set listener for data change. args aren't used
		ip.textProperty().addListener((arg1,arg2,arg3) -> {
			if (selection[0] == SERVER) {
				if (portEmpty()) {
					startButton.setDisable(true);
				} else {
					startButton.setDisable(false);
				}
			} else {
				if (!ipEmpty() && !portEmpty()) {
					startButton.setDisable(false);
				} else {
					startButton.setDisable(true);
				}
			}
			if (toggleGroup.getSelectedToggle() == null) {
				startButton.setDisable(true);
			}
		});
		port.textProperty().addListener((arg1,arg2,arg3) -> {
			if (portEmpty()) {
				startButton.setDisable(true);
			} else if (selection[0] == SERVER) {
				startButton.setDisable(false);
			} else if (!ipEmpty()) {
				startButton.setDisable(false);
			} else {
				startButton.setDisable(true);
			}
			if (toggleGroup.getSelectedToggle() == null) {
				startButton.setDisable(true);
			}
		});

		// Establish start button functionality
		startButton.setOnAction((event) -> {

			System.out.println("Selected: " + selection[0]);
			System.out.println(ip.getText() + ":" + port.getText());
			try {
				nextApplication(ip.getText(),port.getText(), selection[0],primaryStage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private boolean portEmpty() {
		return port.getText().isEmpty();
	}

	private boolean ipEmpty() {
		return ip.getText().isEmpty();
	}

	private void nextApplication(String ip, String port, Selection selection,Stage currentStage) throws IOException {
		currentStage.close();
		switch(selection) {
			case CLIENT:
				startClient(ip, port);
				break;
			case SERVER:
				ServerController serverApp = new ServerController(Integer.parseInt(port));
				serverApp.start(new Stage());
				break;
		}
	}

	private void startClient(String ip, String port) throws IOException {
		final LoginController loginApp = new LoginController(ip, Integer.parseInt(port));
		loginApp.start(new Stage());
	}
}