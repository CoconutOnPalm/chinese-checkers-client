package com.chinese_checkers.ui;

import javafx.scene.control.Label;

/**
 * A label that represents a player in the game lobby.
 */
public class PlayerLabel extends Label
{
	public PlayerLabel(final String username)
	{
		super(username);
		this.setFont(javafx.scene.text.Font.font(30));
		this.setPrefWidth(268);
		this.setPrefHeight(46);
		this.setAlignment(javafx.geometry.Pos.CENTER);
		this.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: #ececec;");
		this.setStyle("-fx-opacity: 1;");
	}

	/**
	 * Sets the label to be selected or deselected, changing the background color.
	 */
	public void setSelected(final boolean selected)
	{
		if (selected)
		{
			this.setStyle("-fx-background-color: #ccffcc;");
		}
		else
		{
			this.setStyle("-fx-background-color: #ececec;");
		}
	}
}
