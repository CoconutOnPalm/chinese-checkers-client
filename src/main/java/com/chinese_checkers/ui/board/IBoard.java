package com.chinese_checkers.ui.board;

import com.chinese_checkers.comms.Player;
import com.chinese_checkers.comms.Position;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Map;

public interface IBoard
{
	Map<Position, IBoardObject> getTiles();
	IBoardObject getTile(Position position);
	IBoardObject getTileByCanvasPosition(float x, float y);

	Map<Position, Point2D> mapCanvasPositions();
	Map<Player.Corner, Color> mapPlayerColors();
}
