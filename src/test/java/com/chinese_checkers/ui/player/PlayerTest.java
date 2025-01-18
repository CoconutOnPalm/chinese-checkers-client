package com.chinese_checkers.ui.player;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest
{
	@Test
	void testGetters()
	{
		Player player = new Player(1, "Alice");
		assertEquals(1, player.getID());
		assertEquals("Alice", player.getName());
	}

	@Test
	void testAddPawn()
	{
		Player player = new Player(1, "Alice");

		for (int i = 0; i < 10; i++)
		{
			Pawn pawn = new Pawn(i, player.getID(), Color.BLACK);
			player.addPawn(pawn);
			assertEquals(pawn, player.getPawn(i));
		}

		assertEquals(10, player.getPawns().size());
	}
}