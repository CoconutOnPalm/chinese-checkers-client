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
	private final int ownerID;
	private Color color;

	private boolean border = false;

	private Position boardPosition;
	private Point2D canvasPosition;

	public Pawn(final int id, final int ownerID, final Color color)
	{
		super(RADIUS);
		setFill(color);

		this.id	= id;
		this.ownerID = ownerID;
		this.color = color;
	}


	@Override
	public boolean contains(double x, double y)
	{
		return (x - canvasPosition.getX()) * (x - canvasPosition.getX()) + (y - canvasPosition.getY()) * (y - canvasPosition.getY()) <= RADIUS * RADIUS;
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
	public void setHovered(final boolean selected)
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

		if (border)
		{
			gc.setStroke(Color.BLACK);
			gc.strokeOval(canvasPosition.getX() - RADIUS + offsetX, canvasPosition.getY() - RADIUS + offsetY, RADIUS * 2, RADIUS * 2);
		}

		gc.restore();
	}


	public void setBorder(boolean border)
	{
		this.border = border;
	}


	public int getID()
	{
		return id;
	}

	public int getOwnerID()
	{
		return ownerID;
	}
}
