package com.chinese_checkers;

import com.chinese_checkers.game.Game;
import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.ui.PlayerData;
import com.chinese_checkers.ui.UIManager;
import com.chinese_checkers.ui.board.IBoardObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
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

		uiManager = new UIManager(playerBoard, chatlog, canvas);

		game = new Game(uiManager);
		game.requestJoin(PlayerData.username);

		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMouseClickedCanvas);
		canvas.addEventHandler(MouseEvent.ANY, this::onMouseMovedCanvas);
		canvas.addEventHandler(MouseEvent.ANY, this::checkBoardMouseHover);

		Platform.runLater(() -> {
			Stage stage = (Stage) playerBoard.getScene().getWindow();
			stage.setOnCloseRequest(e -> NetworkConnector.disconnect());
		});

	}

	public void checkBoardMouseHover(MouseEvent event)
	{
		if (game.getBoardManager() == null)
			return;

		var board = game.getBoardManager().getBoard();
		board.getTiles().values().forEach(tile -> tile.setSelected(false));

		IBoardObject tile = board.getTileByCanvasPosition((float)event.getX(), (float)event.getY());
		if (tile != null)
		{
			tile.setSelected(true);
		}
	}


	public void onMouseClickedCanvas(MouseEvent event)
	{
		System.out.println("Canvas clicked at (" + event.getX() + ", " + event.getY() + ")");
	}

	public void onMouseMovedCanvas(MouseEvent event)
	{
		this.render();
	}

	public void render()
	{
		final float offsetX = canvas.widthProperty().floatValue() / 2;
		final float offsetY = canvas.heightProperty().floatValue() / 2;

		game.renderBoard(canvas.getGraphicsContext2D(), offsetX, offsetY);
	}
}
