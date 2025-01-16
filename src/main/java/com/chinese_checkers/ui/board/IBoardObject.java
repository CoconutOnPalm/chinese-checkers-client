package com.chinese_checkers.ui.board;

import com.chinese_checkers.comms.Position;
import javafx.geometry.Point2D;

public interface IBoardObject
{
	void setBoardPosition(final Position position, final IBoard board);
	Position getBoardPosition();

	void setSelected(final boolean selected);
}
