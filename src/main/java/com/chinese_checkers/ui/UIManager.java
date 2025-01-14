package com.chinese_checkers.ui;

import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UIManager
{
	ChatManager chatManager;

	private VBox playerBoard;
	Map<Integer, PlayerLabel> playerLabels;

	public UIManager(VBox playerBoard, TextArea chatlog)
	{
		this.chatManager = new ChatManager(chatlog);

		this.playerBoard = playerBoard;
		playerLabels = new HashMap<>();

		// Add player labels for testing
		for (int i = 0; i < 3; i++)
		{
			PlayerLabel playerLabel = new PlayerLabel("Player " + i);
			playerLabels.put(i, playerLabel);
			playerBoard.getChildren().add(playerLabel);
		}
	}

	public void addPlayer(int playerID, String playerName)
	{
		PlayerLabel playerLabel = new PlayerLabel(playerName);
		playerLabels.put(playerID, playerLabel);
		playerBoard.getChildren().add(playerLabel);
	}

	public void removePlayer(int playerID)
	{
		PlayerLabel playerLabel = playerLabels.get(playerID);
		playerBoard.getChildren().remove(playerLabel);
		playerLabels.remove(playerID);
	}

	public void selectPlayer(int playerID)
	{
		for (PlayerLabel playerLabel : playerLabels.values())
		{
			playerLabel.setSelected(false);
		}

		playerLabels.get(playerID).setSelected(true);
	}


	public void addMessage(String message)
	{
		var time = LocalDateTime.now();
		int hour = time.getHour();
		int minute = time.getMinute();

		chatManager.addMessage("[" + hour + ":" + minute + "]> " + message);
	}
}
