package com.chinese_checkers.ui.board;

import javafx.scene.canvas.GraphicsContext;

/**
 * The BoardManager class manages the rendering of the board tiles.
 */
public class BoardManager
{
	private final IBoard board;

	public BoardManager(final IBoard board)
	{
		this.board = board;
	}

	public IBoard getBoard()
	{
		return board;
	}


	public void renderTiles(final GraphicsContext gc, final float offsetX, final float offsetY)
	{
		for (var tile : board.getTiles().values())
		{
			if (tile instanceof IDrawable)
			{
				((IDrawable) tile).draw(gc, offsetX, offsetY);
			}
		}
	}
}
