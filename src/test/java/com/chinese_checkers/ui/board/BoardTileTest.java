package com.chinese_checkers.ui.board;

import com.chinese_checkers.comms.Position;
import com.chinese_checkers.ui.player.Pawn;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTileTest
{
	private static final float TILE_RADIUS = 1f;


	@Test
	void testContains()
	{
		BoardTile tile = new BoardTile(new Point2D(0, 0), new Position(0, 0), TILE_RADIUS);

		float inner_radius = (float) (TILE_RADIUS * Math.sqrt(3) / 2f);
		for (float x = -TILE_RADIUS; x <= TILE_RADIUS; x += 0.01f)
		{
			for (float y = -TILE_RADIUS; y <= TILE_RADIUS; y += 0.01f)
			{
				if (x * x + y * y <= inner_radius * inner_radius)
				{
					assertTrue(tile.contains(x, y));
				}
			}
		}
	}
}