package com.chinese_checkers.ui;

import javafx.scene.control.TextArea;

public class ChatManager
{
	private final TextArea chatlog;

	public ChatManager(TextArea chatlog)
	{
		this.chatlog = chatlog;
	}

	public void addMessage(String message)
	{
		chatlog.appendText(message + "\n");
	}
}
