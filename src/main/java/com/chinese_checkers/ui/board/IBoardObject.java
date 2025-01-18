package com.chinese_checkers.ui.board;

import com.chinese_checkers.comms.Position;

/**
 * The IBoardObject interface defines the methods for interacting with objects on the game board.
 */
public interface IBoardObject
{
	/**
	 * @param position board coordinates
	 * @param board    current board
	 * Set the position of the object on the board in board coordinates.
	 */
	void setBoardPosition(final Position position, final IBoard board);

	/**
	 * Get the position of the object on the board in board coordinates.
	 * @return board coordinates
	 */
	Position getBoardPosition();

	/**
	 * set the object as "marked" to change its appearance when mouse is above it
	 */
	void setHovered(final boolean selected);
}
