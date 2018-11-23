package com.files4Dayz.application;

import com.files4Dayz.server.Server;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

public class ServerController extends Application {

	private File downloadPath;

	private final ObservableList<FileInfo> data = FXCollections.observableArrayList();

	private Server server;

	@FXML
	private TableView tableView;
	@FXML
	private TableColumn nameCol;
	@FXML
	private TableColumn typeCol;
	@FXML
	private TableColumn sizeCol;

	public ServerController() throws IOException {

	}

	public ServerController(int port) throws IOException {
		downloadPath = null;
		server = new Server(port);
		server.runAsServer();
	}

	@Override
	public void start(final Stage primaryStage) throws IOException {
		primaryStage.setTitle("Filez4Dayz Server");
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerApp.fxml"));
		System.out.println("loading");
		final Parent root = loader.load();
		System.out.println("LOADWDDD");
		final Scene scene = new Scene(root);

		final MenuBar menuBar = (MenuBar) root.lookup("#menuBar");
		final MenuItem settingsButton = menuBar.getMenus().get(0).getItems().get(0);
		settingsButton.setOnAction((event) -> {
			final SettingsController settings = new SettingsController();
			try {
				settings.start(new Stage(), this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		// Setup table
		tableView = (TableView) loader.getNamespace().get("tableView");
		nameCol = (TableColumn) tableView.getColumns().get(0);
		typeCol = (TableColumn) tableView.getColumns().get(1);
		sizeCol = (TableColumn) tableView.getColumns().get(2);
		// Setup table data
		nameCol.setCellValueFactory(new PropertyValueFactory<File,String>("name"));
		typeCol.setCellValueFactory(new PropertyValueFactory<File,String>("extension"));
		sizeCol.setCellValueFactory(new PropertyValueFactory<File,String>("size"));
		tableView.setItems(data);

		primaryStage.setScene(scene);
		primaryStage.show();
		System.out.println("Server window should be visible");
		listen();
	}

	private void listen() throws IOException {
	    Listener listener = new Listener(server, data);
        listener.start();
	}

	public void addAll(File... files) {

	}

	protected File getDownloadPath() {
		return downloadPath;
	}

	protected void setDownloadPath(File downloadPath) {
		this.downloadPath = downloadPath;
	}}

	class Listener extends Thread {
        private Server server;
        private ObservableList<FileInfo> data;

        Listener(Server server, ObservableList<FileInfo> data) {
            this.server = server;
            this.data = data;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    listen();
                } catch (Exception e) {
                	e.printStackTrace();
                	System.exit(1);
				}
            }
        }

        public void listen() throws IOException {
            FileInfo file = null;
            while ((file = server.saveFile()) != null) {
                data.add(file);
                file = null;
            }
        }
}


