package com.chinese_checkers.ui.board;

import javafx.scene.canvas.GraphicsContext;

/**
 * The IDrawable interface defines the method for drawing objects on the game board.
 */
public interface IDrawable
{
	/**
	 * Draws the object on the game board.
	 *
	 * @param gc 		canvas graphics context
	 * @param offsetX 	distance from the left edge of the canvas to the left edge of the object
	 * @param offsetY 	distance from the top edge of the canvas to the top edge of the object
	 */
	public
	void draw(final GraphicsContext gc, final float offsetX, final float offsetY);
}
