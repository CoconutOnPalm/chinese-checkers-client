package com.chinese_checkers;

import com.chinese_checkers.game.Game;
import com.chinese_checkers.ui.GlobalStageData;
import com.chinese_checkers.ui.PlayerLabel;
import com.chinese_checkers.ui.UIManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameSceneController
{
	@FXML
	private VBox playerBoard;
	@FXML
	private TextArea chatlog;
	@FXML
	private Canvas canvas;


	Game game;
	UIManager uiManager;


	public void initialize()
	{
		System.out.println("GameSceneController initialized.");

		uiManager = new UIManager(playerBoard, chatlog);

		game = new Game(uiManager);
		game.requestJoin(GlobalStageData.username);

		Platform.runLater(() -> {
			Stage stage = (Stage) playerBoard.getScene().getWindow();
			stage.setOnCloseRequest(e -> GlobalStageData.networkConnector.disconnect());
		});
	}
}
