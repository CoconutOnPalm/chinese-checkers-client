package com.chinese_checkers.ui.board.builtin;

import com.chinese_checkers.comms.Position;
import com.chinese_checkers.ui.board.BoardTile;
import com.chinese_checkers.ui.board.IBoard;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultBoardTest
{
	private static final float TILE_RADIUS = 1f;

	@Test
	void testTilePositioning()
	{
		for (int i = 2; i <= 10; i++)
		{
			IBoard board = new DefaultBoard(new Point2D(0, 0), TILE_RADIUS, i);
			int expectedTileCount = 1 + 6 * i * (i - 1);
			assertEquals(expectedTileCount, board.getTiles().size());
		}

		// test for the smallest board size (I'm lazy)

		IBoard board = new DefaultBoard(new Point2D(0, 0), TILE_RADIUS, 2);
		assertEquals(13, board.getTiles().size());

		assertNotNull(board.getTile(new Position(1, 2)));

		assertNotNull(board.getTile(new Position(-1, 1)));
		assertNotNull(board.getTile(new Position(0, 1)));
		assertNotNull(board.getTile(new Position(1, 1)));
		assertNotNull(board.getTile(new Position(2, 1)));

		assertNotNull(board.getTile(new Position(-1, 0)));
		assertNotNull(board.getTile(new Position(0, 0)));
		assertNotNull(board.getTile(new Position(1, 0)));

		assertNotNull(board.getTile(new Position(-2, -1)));
		assertNotNull(board.getTile(new Position(-1, -1)));
		assertNotNull(board.getTile(new Position(0, -1)));
		assertNotNull(board.getTile(new Position(1, -1)));

		assertNotNull(board.getTile(new Position(-1, -2)));

		// ---

		assertNotNull(board.getTileByCanvasPosition(0, 0));
	}

}