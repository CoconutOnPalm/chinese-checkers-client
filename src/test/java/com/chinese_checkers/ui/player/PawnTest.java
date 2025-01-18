package com.chinese_checkers.ui.player;

import com.chinese_checkers.comms.Position;
import com.chinese_checkers.ui.board.IBoard;
import com.chinese_checkers.ui.board.IBoardObject;
import com.chinese_checkers.ui.board.builtin.DefaultBoard;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest
{
	private IBoard board;
	private static final float TILE_RADIUS = 1f;

	@BeforeEach
	void setUp()
	{
		board = new DefaultBoard(new Point2D(0, 0), TILE_RADIUS, 1);
	}


	@Test
	void testGetters()
	{
		Pawn pawn = new Pawn(1, 1, Color.BLACK);
		assertEquals(1, pawn.getID());
		assertEquals(1, pawn.getOwnerID());
	}

	@Test
	void testBoardPosition()
	{
		for (int i = 1; i <= 10; i++)
		{
			IBoard testBoard = new DefaultBoard(new Point2D(0, 0), TILE_RADIUS, i);

			testBoard.mapCanvasPositions().forEach((position, point2D) -> {
				Pawn pawn = new Pawn(1, 1, Color.BLACK);
				pawn.setBoardPosition(position, testBoard);

				assertEquals(position, pawn.getBoardPosition());
			});
		}
	}
}