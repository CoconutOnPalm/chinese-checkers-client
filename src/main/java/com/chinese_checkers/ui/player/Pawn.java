package com.chinese_checkers.ui.player;

import com.chinese_checkers.comms.Position;
import com.chinese_checkers.ui.board.IBoard;
import com.chinese_checkers.ui.board.IBoardObject;
import com.chinese_checkers.ui.board.IDrawable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 * @brief Class representing a pawn on the game board.
 */
public class Pawn extends Circle implements IDrawable, IBoardObject
{
	private static final int RADIUS = 20;

	private final int id;
	private final int ownerID;
	private final Color color;

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
	public boolean contains(final double x, final double y)
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
	public void draw(final GraphicsContext gc, final float offsetX, final float offsetY)
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


	/**
	 * @brief Sets if the pawn should have a border.
	 */
	public void setBorder(final boolean border)
	{
		this.border = border;
	}


	/**
	 * @brief Gets the unique ID of the pawn. Don't confuse with javafx's getId()
	 */
	public int getID()
	{
		return id;
	}

	/**
	 * @brief Gets the ID of the player that owns the pawn.
	 */
	public int getOwnerID()
	{
		return ownerID;
	}
}
