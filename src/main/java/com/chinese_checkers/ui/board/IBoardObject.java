package com.chinese_checkers.ui.board;

import com.chinese_checkers.comms.Position;

public interface IBoardObject
{
	void setBoardPosition(final Position position, final IBoard board);
	Position getBoardPosition();

	void setHovered(final boolean selected);
}
