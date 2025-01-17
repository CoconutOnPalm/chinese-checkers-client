package com.chinese_checkers;

import com.chinese_checkers.game.Game;
import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.ui.PlayerData;
import com.chinese_checkers.ui.UIManager;
import com.chinese_checkers.ui.board.IBoardObject;
import com.chinese_checkers.ui.player.Pawn;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameSceneController
{
	@FXML
	private VBox playerBoard;
	@FXML
	private TextArea chatlog;
	@FXML
	private Canvas canvas;


	Game game;
	UIManager uiManager;

	Pawn hoveredPawn = null; // for performance upgrade (*laughing emoji*)
	IBoardObject hoveredTile = null;

	Pawn selectedPawn = null;


	public void initialize()
	{
		System.out.println("GameSceneController initialized.");

		uiManager = new UIManager(playerBoard, chatlog, canvas);

		game = new Game(uiManager);
		game.requestJoin(PlayerData.username);

		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMouseClickedCanvas);
		canvas.addEventHandler(MouseEvent.ANY, this::onMouseMovedCanvas);
		canvas.addEventHandler(MouseEvent.ANY, this::checkBoardMouseHover);

		Platform.runLater(() -> {
			Stage stage = (Stage) playerBoard.getScene().getWindow();
			stage.setOnCloseRequest(e -> NetworkConnector.disconnect());
			stage.setTitle("Chinese Checkers - " + PlayerData.username);
		});

	}

	public void checkBoardMouseHover(MouseEvent event)
	{
		// tile
		if (game.getBoardManager() == null)
			return;

		var board = game.getBoardManager().getBoard();
		board.getTiles().values().forEach(tile -> tile.setHovered(false));
		hoveredTile = null;

		IBoardObject tile = board.getTileByCanvasPosition((float)event.getX(), (float)event.getY());
		if (tile != null)
		{
			tile.setHovered(true);
			hoveredTile = tile;
		}

		// pawn
		hoveredPawn = null;
		game.getPlayers().forEach((id, player) -> {
			player.getPawns().forEach((pos, pawn) -> {
				pawn.setHovered(false);
			});
		});

		game.getPlayers().forEach((id, player) -> {
			player.getPawns().forEach((pos, pawn) -> {
				if (pawn != null && pawn.contains((float)event.getX(), (float)event.getY()))
				{
					pawn.setHovered(true);
					hoveredPawn = pawn;
				}
			});
		});
	}


	public void onMouseClickedCanvas(MouseEvent event)
	{
		if (hoveredTile == null || game.getBoardManager() == null)
		{
			if (selectedPawn != null)
			{
				selectedPawn.setBorder(false);
				selectedPawn = null;
			}

			return;
		}

		if (selectedPawn == null)
		{
			if (hoveredPawn == null || hoveredPawn.getOwnerID() != PlayerData.id)
				return;

			selectedPawn = hoveredPawn;
			selectedPawn.setBorder(true);
		}
		else
		{
			var pawns = game.getPlayers().get(PlayerData.id).getPawns();

			// select your other pawn
			if (pawns.containsValue(hoveredPawn))
			{
				selectedPawn.setBorder(false);
				selectedPawn = hoveredPawn;
				selectedPawn.setBorder(true);
				return;
			}

			// check if the tile is not occupied

			for (var player : game.getPlayers().values())
			{
				for (var pawn : player.getPawns().values())
				{
					if (pawn.getBoardPosition().equals(hoveredTile.getBoardPosition()))
					{
						return;
					}
				}
			}

			selectedPawn.setBorder(false);
			System.out.println("Moving pawn " + selectedPawn.getID() + " to " + hoveredTile.getBoardPosition());
			game.moveLocally(selectedPawn, hoveredTile.getBoardPosition());

			selectedPawn = null;
			// TODO: notify Game class
		}
	}

	public void onMouseMovedCanvas(MouseEvent event)
	{
		this.render();
	}

	public void render()
	{
		final float offsetX = canvas.widthProperty().floatValue() / 2;
		final float offsetY = canvas.heightProperty().floatValue() / 2;

		game.renderBoard(canvas.getGraphicsContext2D(), offsetX, offsetY);
	}
}
