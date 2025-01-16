package com.chinese_checkers.game;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.FromClient.DisconnectMessage;
import com.chinese_checkers.comms.Message.FromClient.MoveRequestMessage;
import com.chinese_checkers.comms.Message.FromClient.RequestJoinMessage;
import com.chinese_checkers.comms.Message.FromServer.*;
import com.chinese_checkers.comms.Message.Message;
import com.chinese_checkers.networking.CommandParserWrapper;
import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.networking.ServerResponseManager;
import com.chinese_checkers.ui.PlayerData;
import com.chinese_checkers.ui.UIManager;
import com.chinese_checkers.ui.board.BoardManager;
import com.chinese_checkers.ui.board.IBoard;
import com.chinese_checkers.ui.board.builtin.DefaultBoard;
import com.chinese_checkers.ui.player.Pawn;
import com.chinese_checkers.ui.player.Player;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

public class Game
{
	private boolean isRunning = true;
	private final ServerResponseManager responseManager = new ServerResponseManager();
	private final UIManager uiManager;
	private BoardManager boardManager;

	private final Map<Integer, Player> players;


	private int myPlayerID = -1;

	public Game(final UIManager uiManager)
	{
		this.uiManager = uiManager;
		this.players = new HashMap<>();


		CommandParserWrapper.addCommand("game_start", msg -> onGameStart((GameStartMessage) msg));
		CommandParserWrapper.addCommand("game_end", msg -> onGameEnd((GameEndMessage) msg));
		CommandParserWrapper.addCommand("next_round", msg -> onNextRound((NextRoundMessage) msg));
		CommandParserWrapper.addCommand("response", msg -> onServerResponse((ResponseMessage) msg));
		CommandParserWrapper.addCommand("move_player", msg -> onPlayerMoved((MovePlayerMessage) msg));
		CommandParserWrapper.addCommand("self_data", msg -> onSelfDataGiven((SelfDataMessage) msg));

		responseManager.addWaitingResponse("move_request");
	}


	private void addPlayer(int id, String name)
	{
		players.put(id, new Player(id, name));
		uiManager.addPlayer(id, name);
	}


	public void renderBoard(GraphicsContext gc, float offsetX, float offsetY)
	{
		if (boardManager == null)
		{
			return;
		}

		boardManager.renderTiles(gc, offsetX, offsetY);
		this.renderPawns(gc, offsetX, offsetY);
	}


	private void renderPawns(GraphicsContext gc, float offsetX, float offsetY)
	{
		players.forEach((id, player) -> {
			player.getPawns().forEach((pawnID, pawn) -> {
				pawn.draw(gc, 0, 0);
			});
		});
	}


	public void requestJoin(String name)
	{
		if (!NetworkConnector.isConnected())
		{
			System.out.println("Not connected to a server.");
			return;
		}

		if (name == null || name.isEmpty())
		{
			System.out.println("Invalid name.");
			return;
		}

		Message msg = new RequestJoinMessage(name);
		var json = msg.toJson();
		if (json == null)
		{
			System.out.println("Failed to create JSON message.");
			return;
		}

		uiManager.addMessage("Requesting to join the game...");
		NetworkConnector.send(json);
	}


	public void moveLocally(String line)
	{
		if (line == null || line.isEmpty())
		{
			System.out.println("Invalid move command.");
			return;
		}

		// Usage: move <pawn> <s> <q> <r>
		String[] args = line.split(" ");

		if (args.length != 4)
		{
			System.out.println("Usage: move <pawn> <s> <q> <r>");
			return;
		}

		int pawn;
		int x = 0, y = 0;

		try
		{
			pawn = Integer.parseInt(args[0]);
		} catch (NumberFormatException e)
		{
			System.out.println("Invalid number format.");
			return;
		}

		if (!NetworkConnector.isConnected())
		{
			System.out.println("Not connected to a server.");
			return;
		}

		Message msg = new MoveRequestMessage(pawn, x, y);
		String json = msg.toJson();

		if (json == null)
		{
			System.out.println("Failed to create JSON message.");
			return;
		}

		NetworkConnector.send(json);

		// await server response
		String status = responseManager.waitForResponse("move_request", 10);

		if (status == null || !status.equals("success"))
		{
			System.out.println("Move failed.");
			// TODO: revert move
			return;
		}
		else
		{
			System.out.println("Move successful.");
		}
	}


	private void onServerResponse(ResponseMessage json)
	{
		if (json == null)
		{
			System.out.println("Invalid server response.");
			return;
		}

		responseManager.pushResponse(json);
	}



	private void onGameStart(GameStartMessage json)
	{
		int boardSize = json.getBoardSize();
		this.boardManager = new BoardManager(new DefaultBoard(new Point2D(uiManager.getCanvasSize().getX() / 2f, uiManager.getCanvasSize().getY() / 2f), 25, boardSize));

		IBoard board = boardManager.getBoard();

		json.getPawns().forEach((pos, pawn) -> {
			int ownerID = pawn.getOwner().getId();
			int pawnID = pawn.getId();
			var color = pawn.getOwner().getCorner();

			if (!players.containsKey(ownerID))
			{
				System.out.println("[WARNING]: player not present; adding player ID=" + ownerID + ": " + pawn.getOwner().getName());
				addPlayer(ownerID, pawn.getOwner().getName());
			}

			Pawn p = new Pawn(pawnID, board.mapPlayerColors().get(color));
			p.setBoardPosition(pos, board);
			players.get(ownerID).addPawn(p);
		});

		uiManager.addMessage("Game started.");
	}

	private void onGameEnd(GameEndMessage json)
	{
		// Parse JSON and end the game

		System.out.println("Game ended.");
		isRunning = false;
	}

	private void onNextRound(NextRoundMessage json)
	{
		// Parse JSON and start the next round

		System.out.println("Next round started.");
	}

	private void fetchBoard(String json)
	{
		// Parse JSON and update board

		System.out.println("Fetching board...");
	}

	private void onPlayerMoved(MovePlayerMessage json)
	{
		int playerID = json.playerID;
		int pawnID = json.pawnID;
		int x = json.x;
		int y = json.y;

		if (playerID == myPlayerID)
		{
			// check if this pawn is at the correct position
			// if not, do what the server says
			return;
		}

		System.out.println("Moving player ID=" + playerID + ": pawn ID=" + pawnID + " to (" + x + ", " + y + ")");
	}

	private void onSelfDataGiven(SelfDataMessage json)
	{
		myPlayerID = json.getPlayerID();

		System.out.println("Updating data: ID=" + myPlayerID);
	}

	public BoardManager getBoardManager()
	{
		return boardManager;
	}
}
