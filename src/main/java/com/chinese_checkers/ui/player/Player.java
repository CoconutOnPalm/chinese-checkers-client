package com.chinese_checkers.ui.player;

import java.util.HashMap;

public class Player
{
	private final int id;
	private final String name;

	private final HashMap<Integer, Pawn> pawns;

	public Player(int id, String name)
	{
		this.id = id;
		this.name = name;
		this.pawns = new HashMap<>();
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void addPawn(Pawn pawn)
	{
		pawns.put(pawn.getID(), pawn);
	}

	public Pawn getPawn(int id)
	{
		return pawns.get(id);
	}

	public HashMap<Integer, Pawn> getPawns()
	{
		return pawns;
	}
}
