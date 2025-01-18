package com.chinese_checkers.ui.board;

import com.chinese_checkers.comms.Player;
import com.chinese_checkers.comms.Position;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * The IBoard interface defines the methods for interacting with the game board in Chinese Checkers.
 */
public interface IBoard
{
	/**
	 * @return all tiles on the board
	 */
	Map<Position, IBoardObject> getTiles();

	/**
	 * @param position board position
	 * @return the tile at the specified board position
	 * @brief Get the tile at the specified board position
	 */
	IBoardObject getTile(final Position position);

	/**
	 * @brief Returns the tile at the specified canvas position
	 * @param x
	 * @param y
	 */
	IBoardObject getTileByCanvasPosition(final float x, final float y);

	/**
	 * @brief returns a map of board positions to canvas positions
	 */
	Map<Position, Point2D> mapCanvasPositions();

	/**
	 * @brief returns a map of player corners to colors
	 */
	Map<Player.Corner, Color> mapPlayerColors();
}
