package com.chinese_checkers.ui.player;

import com.chinese_checkers.comms.Position;
import com.chinese_checkers.ui.board.IBoard;
import com.chinese_checkers.ui.board.IBoardObject;
import com.chinese_checkers.ui.board.IDrawable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Pawn extends Circle implements IDrawable, IBoardObject
{
	private static final int RADIUS = 20;

	private int id;
	private Color color;

	private Position boardPosition;
	private Point2D canvasPosition;

	public Pawn(final int id, final Color color)
	{
		super(RADIUS);
		setFill(color);

		this.id	= id;
		this.color = color;
	}

	@Override
	public void setBoardPosition(final Position position, final IBoard board)
	{
		this.boardPosition = position;

		canvasPosition = board.mapCanvasPositions().get(position);
	}

	@Override
	public Position getBoardPosition()
	{
		return boardPosition;
	}


	@Override
	public void setSelected(final boolean selected)
	{
		setFill(selected ? color.brighter() : color);
	}

	@Override
	public void draw(GraphicsContext gc, float offsetX, float offsetY)
	{
		if (canvasPosition == null)
		{
			System.out.println("(Pawn): Canvas position is null");
			return;
		}

		gc.save();

		gc.setFill(this.getFill());
		gc.fillOval(canvasPosition.getX() - RADIUS + offsetX, canvasPosition.getY() - RADIUS + offsetY, RADIUS * 2, RADIUS * 2);

		gc.restore();
	}


	public int getID()
	{
		return id;
	}
}
