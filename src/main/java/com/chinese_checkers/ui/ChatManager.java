package com.chinese_checkers.ui;

import javafx.scene.control.TextArea;

/**
 * The ChatManager class manages the chat log in the game UI.
 */
public class ChatManager
{
	private final TextArea chatlog;

	public ChatManager(final TextArea chatlog)
	{
		this.chatlog = chatlog;
	}

	/**
	 * Adds a message to the chat log.
	 */
	public void addMessage(final String message)
	{
		chatlog.appendText(message + "\n");
	}
}
