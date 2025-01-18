package com.chinese_checkers.ui;

import com.chinese_checkers.comms.Message.FromServer.NextRoundMessage;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manager for the UI elements of the game
 */
public class UIManager
{
	private static final ReentrantLock lock = new ReentrantLock();

	private final ChatManager chatManager;

	private final VBox playerBoard;
	private final Canvas canvas;
	private final Button skipRoundButton;
	private final Map<Integer, PlayerLabel> playerLabels;
	private int currentlySelected = -1;

	public UIManager(final VBox playerBoard, final TextArea chatlog, final Canvas canvas, final Button skipRoundButton)
	{
		this.chatManager = new ChatManager(chatlog);

		this.playerBoard = playerBoard;
		playerLabels = new HashMap<>();
		this.canvas = canvas;
		this.skipRoundButton = skipRoundButton;
	}

	/**
	 * Add a player label to the player board
	 * @param playerID
	 * @param playerName
	 */
	public void addPlayer(final int playerID, final String playerName)
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
	}


	/**
	 * Add a temporary player label to avoid null pointer exceptions in other methods
	 * @param playerID
	 */
	private void addGhostPlayer(final int playerID)
	{
		lock.lock();

		// if player not yet added, add an empty label
		if (playerLabels.containsKey(playerID))
			return;

		playerLabels.put(playerID, new PlayerLabel(""));

		lock.unlock();
	}

	public void removePlayer(final int playerID)
	{
		Platform.runLater(() -> {

			lock.lock();

			PlayerLabel playerLabel = playerLabels.get(playerID);
			playerBoard.getChildren().remove(playerLabel);
			playerLabels.remove(playerID);

			lock.unlock();
		});
	}

	public void selectPlayer(final int playerID)
	{
		lock.lock();

		for (PlayerLabel playerLabel : playerLabels.values())
		{
			playerLabel.setSelected(false);
		}

		playerLabels.get(playerID).setSelected(true);

		lock.unlock();
	}

	/**
	 * Select the player whose turn it is
	 * @param json 	Server message containing the player ID
	 */
	public void selectPlayer(final NextRoundMessage json)
	{
		lock.lock();
		int currentPlayerID = json.getCurrentPlayerID();

		for (PlayerLabel playerLabel : playerLabels.values())
		{
			playerLabel.setSelected(false);
		}

		if (!playerLabels.containsKey(currentPlayerID))
		{
			addGhostPlayer(currentPlayerID);
		}

		currentlySelected = currentPlayerID;
		playerLabels.get(currentPlayerID).setSelected(true);

		lock.unlock();
	}


	/**
	 * Add a time-stamped message to the chat log
	 * @param message
	 */
	public void addMessage(final String message)
	{
		var time = LocalDateTime.now();
		int hour = time.getHour();
		int minute = time.getMinute();

		if (minute < 10)
			chatManager.addMessage("[" + hour + ":0" + minute + "]> " + message);
		else
			chatManager.addMessage("[" + hour + ":" + minute + "]> " + message);
	}


	/**
	 * Disables canvas and skip round button
	 * @param block
	 */
	public void disableUI(final boolean block)
	{
		canvas.setDisable(block);
		skipRoundButton.setDisable(block);
	}

	public Point2D getCanvasSize()
	{
		return new Point2D(canvas.getWidth(), canvas.getHeight());
	}
}
