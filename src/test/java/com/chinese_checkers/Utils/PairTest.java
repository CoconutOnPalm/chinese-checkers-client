package com.chinese_checkers.Utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PairTest
{
	@Test
	void testToString()
	{
		Pair<Integer, Integer> pair = new Pair<>(1, 2);
		assertEquals("(1, 2)", pair.toString());
	}
}