package com.chinese_checkers.ui.board.builtin;

import com.chinese_checkers.comms.Player;
import com.chinese_checkers.comms.Position;
import com.chinese_checkers.ui.board.BoardTile;
import com.chinese_checkers.ui.board.IBoard;
import com.chinese_checkers.ui.board.IBoardObject;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class DefaultBoard implements IBoard
{
	private int BOARD_RADIUS = 5; // default
	private float TILE_RADIUS = 25; // default
	private static final float sqrt3_div2 = (float) Math.sqrt(3) / 2; // sqrt(3) / 2

	private Map<Position, IBoardObject> tiles;
	private Map<Position, Point2D> positionMap;

	public DefaultBoard(final Point2D center, final float tileRadius, final int boardSize)
	{
		this.BOARD_RADIUS = boardSize;
		this.TILE_RADIUS = tileRadius;

		// tile position relative to the center of the canvas
		tiles = new HashMap<>();
		positionMap = new HashMap<>();

		final float r = TILE_RADIUS;
		final int p = BOARD_RADIUS - 1; // used to determine the position of the tiles during generation

		// __
		// \/
 		// generate this triangle first
		for (int y = p; y >= -(p * 2); y--)
		{
			for (int x = -p; x <= p * 2 + (y - p); x++)
			{
				Position boardPosition = new Position(x, y);
				Point2D position = new Point2D(r * (x * sqrt3_div2 * 2 - y * sqrt3_div2) + center.getX(), 3. / 2. * r * y + center.getY());
				positionMap.put(boardPosition, position);
				tiles.put(boardPosition, new BoardTile(position, boardPosition, TILE_RADIUS));
			}
		}

		// /\
 		// --
 		// generate this triangle second (skip tiles that are already generated)
		for (int y = p * 2; y >= -p; y--)
		{
			for (int x = p; x >= -(p * 2) + (y + p); x--)
			{
				Position boardPosition = new Position(x, y);
				Point2D position = new Point2D(r * (x * sqrt3_div2 * 2 - y * sqrt3_div2) + center.getX(), 3. / 2. * r * y + center.getY());
				if (!tiles.containsKey(boardPosition))
				{
					positionMap.put(boardPosition, position);
					tiles.put(boardPosition, new BoardTile(position, boardPosition, TILE_RADIUS));
				}
			}
		}
	}

	@Override
	public Map<Position, IBoardObject> getTiles()
	{
		return tiles;
	}

	@Override
	public IBoardObject getTile(Position position)
	{
		return tiles.get(position);
	}

	@Override
	public IBoardObject getTileByCanvasPosition(float x, float y)
	{
		for (var tile : tiles.values())
		{
			if (tile instanceof BoardTile)
			{
				BoardTile boardTile = (BoardTile) tile;
				if (boardTile.contains(x, y))
				{
					return boardTile;
				}
			}
		}
		return null;
	}

	@Override
	public Map<Position, Point2D> mapCanvasPositions()
	{
		return positionMap;
	}

	@Override
	public Map<Player.Corner, Color> mapPlayerColors()
	{
		return Map.of(
				Player.Corner.UPPER, Color.RED,
				Player.Corner.LOWER, Color.BLUE,
				Player.Corner.LOWER_LEFT, Color.GREEN,
				Player.Corner.LOWER_RIGHT, Color.YELLOW,
				Player.Corner.UPPER_LEFT, Color.PURPLE,
				Player.Corner.UPPER_RIGHT, Color.ORANGE
		);
	}

}
