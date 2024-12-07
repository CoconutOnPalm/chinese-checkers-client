package com.chinese_checkers.game;

public class Player
{
	private final String name;
	private final int id;
	private final Point[] pawnPositions;

	public Player(String name, int id)
	{
		this.name = name;
		this.id = id;
		this.pawnPositions = new Point[10];
	}

	public String getName()
	{
		return name;
	}

	public int getId()
	{
		return id;
	}

	public Point[] getPawnPositions()
	{
		return pawnPositions;
	}

	public void setPawnPosition(int index, Point position)
	{
		pawnPositions[index] = position;
	}
}
