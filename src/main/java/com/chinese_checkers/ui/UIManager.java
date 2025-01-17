package com.chinese_checkers.ui;

import com.chinese_checkers.comms.Message.FromServer.NextRoundMessage;
import com.chinese_checkers.ui.board.BoardManager;
import com.chinese_checkers.ui.board.IBoardObject;
import com.chinese_checkers.ui.board.builtin.DefaultBoard;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UIManager
{
	ChatManager chatManager;

	private VBox playerBoard;
	private Canvas canvas;
	Map<Integer, PlayerLabel> playerLabels;
	int currentlySelected = -1;

	public UIManager(VBox playerBoard, TextArea chatlog, Canvas canvas)
	{
		this.chatManager = new ChatManager(chatlog);

		this.playerBoard = playerBoard;
		playerLabels = new HashMap<>();
		this.canvas = canvas;
	}

	public void addPlayer(int playerID, String playerName)
	{
		Platform.runLater(() -> {
			PlayerLabel playerLabel = new PlayerLabel(playerName);
			playerLabels.put(playerID, playerLabel);
			playerBoard.getChildren().add(playerLabel);

			if (currentlySelected == playerID)
			{
				playerLabel.setSelected(true);
			}
		});
		// PlayerLabel playerLabel = new PlayerLabel(playerName);
		// playerLabels.put(playerID, playerLabel);

		//Platform.runLater(() -> playerBoard.getChildren().add(playerLabel));
		// playerBoard.getChildren().add(playerLabel);
	}

	private void addGhostPlayer(int playerID, String playerName)
	{
		// if player not yet added, add an empty label
		if (playerLabels.containsKey(playerID))
			return;

		playerLabels.put(playerID, new PlayerLabel(playerName));
	}

	public void removePlayer(int playerID)
	{
		Platform.runLater(() -> {
			PlayerLabel playerLabel = playerLabels.get(playerID);
			playerBoard.getChildren().remove(playerLabel);
			playerLabels.remove(playerID);
		});

		// PlayerLabel playerLabel = playerLabels.get(playerID);
		// playerBoard.getChildren().remove(playerLabel);
		// playerLabels.remove(playerID);
	}

	public void selectPlayer(int playerID)
	{
		for (PlayerLabel playerLabel : playerLabels.values())
		{
			playerLabel.setSelected(false);
		}

		playerLabels.get(playerID).setSelected(true);
	}

	public void selectPlayer(NextRoundMessage json)
	{
		int currentPlayerID = json.getCurrentPlayerID();

		for (PlayerLabel playerLabel : playerLabels.values())
		{
			playerLabel.setSelected(false);
		}

		if (!playerLabels.containsKey(currentPlayerID))
		{
			addGhostPlayer(currentPlayerID, "");
		}

		currentlySelected = currentPlayerID;
		playerLabels.get(currentPlayerID).setSelected(true);
	}


	public void addMessage(String message)
	{
		var time = LocalDateTime.now();
		int hour = time.getHour();
		int minute = time.getMinute();

		chatManager.addMessage("[" + hour + ":" + minute + "]> " + message);
	}

	public Point2D getCanvasSize()
	{
		return new Point2D(canvas.getWidth(), canvas.getHeight());
	}
}
