package com.chinese_checkers.ui.board;

import javafx.scene.canvas.GraphicsContext;

public interface IDrawable
{
	void draw(GraphicsContext gc, float offsetX, float offsetY);
}
