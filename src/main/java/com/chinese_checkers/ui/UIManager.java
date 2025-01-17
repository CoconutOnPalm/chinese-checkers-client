package com.chinese_checkers.ui;

import com.chinese_checkers.comms.Message.FromServer.NextRoundMessage;
import com.chinese_checkers.ui.board.BoardManager;
import com.chinese_checkers.ui.board.IBoardObject;
import com.chinese_checkers.ui.board.builtin.DefaultBoard;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import jdk.jfr.consumer.RecordedEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class UIManager
{
	private static final ReentrantLock lock = new ReentrantLock();

	ChatManager chatManager;

	private VBox playerBoard;
	private Canvas canvas;
	private Button skipRoundButton;
	Map<Integer, PlayerLabel> playerLabels;
	int currentlySelected = -1;

	public UIManager(VBox playerBoard, TextArea chatlog, Canvas canvas, Button skipRoundButton)
	{
		this.chatManager = new ChatManager(chatlog);

		this.playerBoard = playerBoard;
		playerLabels = new HashMap<>();
		this.canvas = canvas;
		this.skipRoundButton = skipRoundButton;
	}

	public void addPlayer(int playerID, String playerName)
	{
		Platform.runLater(() -> {
			lock.lock();
			PlayerLabel playerLabel = new PlayerLabel(playerName);
			playerLabels.put(playerID, playerLabel);
			playerBoard.getChildren().add(playerLabel);

			if (currentlySelected == playerID)
			{
				playerLabel.setSelected(true);
			}

			lock.unlock();
		});
		// PlayerLabel playerLabel = new PlayerLabel(playerName);
		// playerLabels.put(playerID, playerLabel);

		//Platform.runLater(() -> playerBoard.getChildren().add(playerLabel));
		// playerBoard.getChildren().add(playerLabel);
	}

	private void addGhostPlayer(int playerID, String playerName)
	{
		lock.lock();
		// if player not yet added, add an empty label
		if (playerLabels.containsKey(playerID))
			return;

		playerLabels.put(playerID, new PlayerLabel(playerName));

		lock.unlock();
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
		lock.lock();

		for (PlayerLabel playerLabel : playerLabels.values())
		{
			playerLabel.setSelected(false);
		}

		playerLabels.get(playerID).setSelected(true);

		lock.unlock();
	}

	public void selectPlayer(NextRoundMessage json)
	{
		lock.lock();
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

		lock.unlock();
	}


	public void addMessage(String message)
	{
		var time = LocalDateTime.now();
		int hour = time.getHour();
		int minute = time.getMinute();

		if (minute < 10)
			chatManager.addMessage("[" + hour + ":0" + minute + "]> " + message);
		else
			chatManager.addMessage("[" + hour + ":" + minute + "]> " + message);
	}


	public void disableUI(boolean block)
	{
		canvas.setDisable(block);
		skipRoundButton.setDisable(block);
	}

	public Point2D getCanvasSize()
	{
		return new Point2D(canvas.getWidth(), canvas.getHeight());
	}
}
