package com.chinese_checkers.game;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.FromClient.DisconnectMessage;
import com.chinese_checkers.comms.Message.FromClient.MoveRequestMessage;
import com.chinese_checkers.comms.Message.FromClient.RequestJoinMessage;
import com.chinese_checkers.comms.Message.FromServer.*;
import com.chinese_checkers.comms.Message.Message;
import com.chinese_checkers.comms.Position;
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

import java.util.HashMap;
import java.util.Map;

public class Game
{
	private boolean isRunning = true;
	private final ServerResponseManager responseManager = new ServerResponseManager();
	private final UIManager uiManager;
	private BoardManager boardManager;

	private final Map<Integer, Player> players;

	private int currentPlayerID = -1; // id of the player whose turn it is

	private int myPlayerID = -1;

	public Game(final UIManager uiManager)
	{
		this.uiManager = uiManager;
		this.players = new HashMap<>();


		CommandParserWrapper.addCommand("game_start", msg -> onGameStart((GameStartMessage) msg));
		CommandParserWrapper.addCommand("game_end", msg -> onGameEnd((GameEndMessage) msg));
		CommandParserWrapper.addCommand("next_round", msg -> onNextRound((NextRoundMessage) msg));
		CommandParserWrapper.addCommand("next_round", msg -> uiManager.selectPlayer((NextRoundMessage) msg));
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

	public Map<Integer, Player> getPlayers()
	{
		return players;
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


	public void moveLocally(Pawn pawn, Position newPosition)
	{
		if (boardManager == null)
			return;

		if (currentPlayerID != myPlayerID)
		{
			uiManager.addMessage("Not your turn.");
			return;
		}


		Position oldPosition = pawn.getBoardPosition();
		System.out.println("Old position: " + oldPosition + ", new position: " + newPosition);
		pawn.setBoardPosition(newPosition, boardManager.getBoard());

		if (!NetworkConnector.isConnected())
		{
			uiManager.addMessage("Not connected to a server.");
			return;
		}

		Message msg = new MoveRequestMessage(pawn.getID(), newPosition.getX(), newPosition.getY());
		String json = msg.toJson();

		if (json == null)
		{
			uiManager.addMessage("Failed to communicate with server.");
			return;
		}

		NetworkConnector.send(json);

		// await server response
		ResponseMessage response = responseManager.waitForResponse("move_request", 10);

		if (response == null)
		{
			uiManager.addMessage("Server did not respond.");
			pawn.setBoardPosition(oldPosition, boardManager.getBoard());
			return;
		}

		if (response.getStatus() == null)
		{
			if (response.getMessage() != null)
				System.out.println("[E1]: Server response: " + response.getMessage());

			uiManager.addMessage("Invalid server response.");
			pawn.setBoardPosition(oldPosition, boardManager.getBoard());
			return;
		}

		System.out.println('{' + response.getStatus().toString() + " : " + response.getMessage() + '}');

		//	SUCCESS,
		//  SUCCESS_JUMP,
		//  INVALID_MOVE,
		//  OCCUPIED,
		//  OUT_OF_BOUNDS,
		//  INVALID_PAWN,
		//  NOT_YOUR_TURN,
		//  GAME_OVER,
		//  UNREACHABLE,
		//  OUT_OF_GOAL
		switch (response.getStatus())
		{
			case ResponseMessage.Status.SUCCESS -> {
				System.out.println("Move successful.");
			}
			case ResponseMessage.Status.FAILURE -> {
				uiManager.addMessage("Invalid move: " + response.getMessage());
				pawn.setBoardPosition(oldPosition, boardManager.getBoard());
			}
			case ResponseMessage.Status.GAME_OVER -> {
				// TODO: fix
				this.onGameEnd(new GameEndMessage());
			}
			case ResponseMessage.Status.ERROR -> {
				uiManager.addMessage("Server error: " + response.getMessage());
				pawn.setBoardPosition(oldPosition, boardManager.getBoard());
			}
			default -> {
				uiManager.addMessage("Unknown server response.");
				pawn.setBoardPosition(oldPosition, boardManager.getBoard());
			}

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

			Pawn p = new Pawn(pawnID, ownerID, board.mapPlayerColors().get(color));
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
		currentPlayerID = json.getCurrentPlayerID();
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

		if (!players.containsKey(playerID))
		{
			System.out.println("[ERROR]: player does not exist.");
			return;
		}

		Player player = players.get(playerID);
		if (!player.getPawns().containsKey(pawnID))
		{
			System.out.println("[ERROR]: pawn does not exist.");
			return;
		}

		Pawn pawn = player.getPawns().get(pawnID);

		if (playerID == myPlayerID)
		{
			if (pawn.getBoardPosition().equals(new Position(x, y)))
			{
				System.out.println("[DEBUG]: pawn already in position (good).");
			}

			return;
		}

		pawn.setBoardPosition(new Position(x, y), boardManager.getBoard());
	}

	private void onSelfDataGiven(SelfDataMessage json)
	{
		myPlayerID = json.getPlayerID();
		PlayerData.id = myPlayerID;

		System.out.println("Updating data: ID=" + myPlayerID);
	}

	public BoardManager getBoardManager()
	{
		return boardManager;
	}
}
