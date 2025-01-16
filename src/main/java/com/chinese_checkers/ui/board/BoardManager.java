package com.chinese_checkers.ui.board;

import javafx.scene.canvas.GraphicsContext;

public class BoardManager
{
	private final IBoard board;

	public BoardManager(IBoard board)
	{
		this.board = board;
	}

	public IBoard getBoard()
	{
		return board;
	}


	public void renderTiles(GraphicsContext gc, float offsetX, float offsetY)
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
