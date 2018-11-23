package com.files4Dayz.application;

import com.files4Dayz.client.Client;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientController extends Application {

	private Client client;

	@FXML
	private TableView<File> fileTable;

	@FXML
	private TableColumn fileColumn;

	private ObservableList<File> data;

	private String ip;
	private int port;

	public ClientController() {
	}

	public ClientController(String ip, int port) throws IOException {
		data = FXCollections.observableArrayList();
		this.ip = ip;
		this.port = port;
		//TODO Instantiate client with ip and port
		client = new Client(ip, port);
	}

	public Client getClient() { return client; }

	public String getUserName() {
		return client.getUsername();
	}

	public String getUserPass() {
		return client.getPassword();
	}

	@Override
	public void start(final Stage primaryStage) throws IOException {
		primaryStage.setTitle("Filez4Dayz Client");
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientApp.fxml"));
		final Parent root = loader.load();
		final Scene scene = new Scene(root);

		final Label ipPortLabel = (Label) root.lookup("#ipPort");
		ipPortLabel.setText(ip + ":" + port);
		// Establish table objects
		fileTable = (TableView<File>) root.lookup("#fileTable");
		fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		fileColumn = fileTable.getColumns().get(0);

		fileColumn.setCellValueFactory(new PropertyValueFactory<File,String>("absolutePath"));

		fileTable.setItems(data);

		final TextField corruptedCount = (TextField) root.lookup("#corruptedCount");

		final Button sendButton = (Button) root.lookup("#sendButton");
		sendButton.setOnAction((event) -> {
			final boolean isArmored = ((CheckBox) root.lookup("#armoringToggle")).isSelected();
			try {
				if (!corruptedCount.getText().isEmpty()) {
					int corrupted = Integer.parseInt(corruptedCount.getText());
					client.sendCorrupted(corrupted);
				}
				sendFiles(isArmored);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		// Establish menu objects
		final MenuBar menuBar = (MenuBar) root.lookup("#menuBar");
		final MenuItem addFile = menuBar.getMenus().get(0).getItems().get(0);
		final MenuItem addDir = menuBar.getMenus().get(0).getItems().get(1);
		final MenuItem removeButton = menuBar.getMenus().get(1).getItems().get(0);
		addFile.setOnAction((event) -> {
			addFile(primaryStage);
		});

		addDir.setOnAction((event) -> {
			try {
				addDirectory(primaryStage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		removeButton.setOnAction((event) -> {
			removeSelected();
		});

		//Establish key listener
		scene.setOnKeyPressed((key) -> {
			switch(key.getCode()) {
				case DELETE:
					removeSelected();
					break;
				case PLUS:
					addFile.fire();
					break;
			}
		});
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void addFile(Stage primaryStage) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose file");
		final File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			data.add(file);
		}
	}

	private void addDirectory(Stage primaryStage) throws IOException {
		final DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Choose directory");
		final File dir = dirChooser.showDialog(primaryStage);
		if (dir != null) {
			traverseDirectory(dir);
		}
	}

	private void traverseDirectory(File dir) throws IOException {
		Files.walk(Paths.get(dir.toURI()))
				.filter(Files::isRegularFile)
				.forEach((x) -> {
					data.add(x.toFile());
				});
	}

	private void removeSelected() {
		File[] selected = fileTable.getSelectionModel().getSelectedItems().toArray(new File[0]);
		for (int i = 0; i < selected.length; i++) {
			data.remove(selected[i]);
		}
	}

	private void sendFiles(boolean isArmored) throws IOException {
		// TODO tell Client to send files
		File[] selected = fileTable.getSelectionModel().getSelectedItems().toArray(new File[0]);
		for (int i = 0; i < selected.length; i++) {
			client.sendFile(selected[i], isArmored);
		}
		// TODO check if file sent successfully
		//TODO remove successfully transferred files from UI
	}
}
