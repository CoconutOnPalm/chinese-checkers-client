package com.chinese_checkers.ui.board;

import com.chinese_checkers.comms.Position;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

/**
 * Represents a hexagonal tile on the game board.
 */
public class BoardTile extends Polygon implements IDrawable, IBoardObject
{
	private static final float sqrt3_div2 = (float) Math.sqrt(3) / 2; // sqrt(3) / 2
	private static final Color blankTileColor = Color.rgb(192, 192, 192, 1);
	private static final Color selectedTileColor = Color.rgb(216, 216, 216, 0.70);

	private final Position boardPosition;
	private final double[] xPoints = new double[6];
	private final double[] yPoints = new double[6];

	public BoardTile(final Point2D position, final Position boardPosition, float radius)
	{

		super(
				position.getX(), position.getY() - radius,
				position.getX() + radius * sqrt3_div2, position.getY() - radius / 2,
				position.getX() + radius * sqrt3_div2, position.getY() + radius / 2,
				position.getX(), position.getY() + radius,
				position.getX() - radius * sqrt3_div2, position.getY() + radius / 2,
				position.getX() - radius * sqrt3_div2, position.getY() - radius / 2
		);

		if (radius <= 0)
			throw new IllegalArgumentException("Radius must be greater than 0");

		xPoints[0] = position.getX();
		yPoints[0] = position.getY() - radius;

		xPoints[1] = position.getX() + radius * sqrt3_div2;
		yPoints[1] = position.getY() - radius / 2;

		xPoints[2] = position.getX() + radius * sqrt3_div2;
		yPoints[2] = position.getY() + radius / 2;

		xPoints[3] = position.getX();
		yPoints[3] = position.getY() + radius;

		xPoints[4] = position.getX() - radius * sqrt3_div2;
		yPoints[4] = position.getY() + radius / 2;

		xPoints[5] = position.getX() - radius * sqrt3_div2;
		yPoints[5] = position.getY() - radius / 2;

		this.setFill(blankTileColor);

		this.boardPosition = boardPosition;
	}

	/**
	 * Checks if the point is inside the hexagon.
	 * @param x the x coordinate of the point in Node's space
	 * @param y the y coordinate of the point in Node's space
	 * @return	true if the point is inside the hexagon, false otherwise
	 */
	@Override
	public boolean contains(final double x, final double y)
	{
		float a = 0; // tan(slope angle)
		int intersections = 0;


		// p0, p1
		for (int i = 0; i < 6; i++)
		{
			Point2D A = new Point2D(xPoints[i], yPoints[i]);
			Point2D B = new Point2D(xPoints[(i + 1) % 6], yPoints[(i + 1) % 6]);


			if (A.getY() == B.getY())
				continue;


			// out of bounds
			if (x < Math.min(A.getX(), B.getX()) || x > Math.max(A.getX(), B.getX()))
				continue;


			if (A.getX() == B.getX())
			{
				if (x <= A.getX())
					intersections++;

				continue;
			}

			a = (float) (B.getY() - A.getY()) / (float) (B.getX() - A.getX());
			float b = (float) (A.getY() - a * A.getX());

			if (y >= a * x + b)
				intersections++;
		}

		return intersections == 1; // we don't expect complex polygons
	}


	@Override
	public void draw(final GraphicsContext gc, final float offsetX, final float offsetY)
	{
		gc.save();

		gc.setFill(this.getFill());
		gc.fillPolygon(xPoints, yPoints, 6);
		gc.setFill(Color.BLACK);
		gc.strokePolygon(xPoints, yPoints, 6);

		// gc.restore() breaks once in a while, so we catch the exception
		try
		{
			gc.restore();
		} catch (Exception e)
		{
			e.printStackTrace();

		}
	}


	@Override
	public void setBoardPosition(final Position position, final IBoard board)
	{
		throw new UnsupportedOperationException("Cannot set canvas position of a board tile");
	}

	@Override
	public Position getBoardPosition()
	{
		return boardPosition;
	}


	@Override
	public void setHovered(final boolean selected)
	{
		this.setFill(selected ? selectedTileColor : blankTileColor);
	}
}
