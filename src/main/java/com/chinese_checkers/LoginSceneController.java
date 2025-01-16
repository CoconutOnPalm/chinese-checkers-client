package com.chinese_checkers;

import com.chinese_checkers.Utils.ErrorDialog;
import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.ui.PlayerData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Objects;


public class LoginSceneController
{
	@FXML
	private TextField usernameInput;
	@FXML
	private TextField hostnameInput;
	@FXML
	private TextField portInput;

	@FXML
	private Button connectButton;

	@FXML
	private Label errorLabel;
	@FXML
	private ProgressIndicator progressIndicator;




	public void initialize()
	{
		Platform.runLater(() -> {
			Stage stage = (Stage) errorLabel.getScene().getWindow();
			stage.setOnCloseRequest(e -> NetworkConnector.disconnect());
		});
	}


	@FXML
	public void onConnectToServer()
	{
		String username = usernameInput.getText();
		String hostname = hostnameInput.getText();

		int port = 0;
		if (!portInput.getText().matches("\\d*"))
		{
			return;
		}

		try {
			port = Integer.parseInt(portInput.getText());
		} catch (NumberFormatException e) {
			System.out.println("Invalid port number: " + portInput.getText());
			return;
		}

		if (username.isEmpty() || hostname.isEmpty())
		{
			errorLabel.setText("Please fill in all fields.");
			errorLabel.setTextFill(javafx.scene.paint.Color.RED);
			return;
		}

		progressIndicator.setVisible(true);
		connectButton.setDisable(true);
		errorLabel.setText("");

		PlayerData.username = username;
		Thread thread = getConnectionThread(hostname, port);
		thread.start();
	}


	private Thread getConnectionThread(String hostname, int port)
	{
		return new Thread(() -> {

			boolean status = false;
			String errorMessage = "";

			try
			{
				status = NetworkConnector.connect(hostname, port);
			} catch (ConnectException e)
			{
				errorMessage = e.getMessage();
			}

			final boolean finalStatus = status;
			final String finalErrorMessage = errorMessage;

			Platform.runLater(() -> {

				progressIndicator.setVisible(false);
				connectButton.setDisable(false);

				if (finalStatus)
				{
					errorLabel.setText("Connected to the server.");
					errorLabel.setTextFill(javafx.scene.paint.Color.GREEN);

					changeScene("/com/chinese_checkers/gameScene.fxml");
				} else
				{
					errorLabel.setText(finalErrorMessage);
					errorLabel.setTextFill(javafx.scene.paint.Color.RED);
				}
			});
		});
	}


	private void changeScene(String fxmlPath)
	{
		Scene scene;
		try
		{
			scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
		} catch (IOException e)
		{
			ErrorDialog.show("FXML ERROR", "Could not load the gameScene.fxml file.");
			throw new RuntimeException(e);
		}
		Stage stage = (Stage) connectButton.getScene().getWindow();
		stage.setScene(scene);
	}



	@FXML
	private void onNumberFieldChanged(KeyEvent event)
	{
		TextField field = (TextField) event.getSource();
		String text = field.getText();
		if (!text.matches("\\d*"))
		{
			errorLabel.setText("Port must be a number.");
			errorLabel.setTextFill(javafx.scene.paint.Color.RED);
			connectButton.setDisable(true);
		}
		else
		{
			errorLabel.setText("");
			connectButton.setDisable(false);
		}
	}
}
