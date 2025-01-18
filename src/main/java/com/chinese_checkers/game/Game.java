package com.chinese_checkers.game;

import com.chinese_checkers.comms.Message.FromClient.EndTurnMessage;
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

/**
 * @brief The Game class manages the state and logic of a game of Chinese Checkers.
 */
public class Game
{
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
		//CommandParserWrapper.addCommand("game_end", msg -> onGameEnd((GameEndMessage) msg));
		CommandParserWrapper.addCommand("announce_winner", msg -> onWinnerAnnounced((AnnounceWinnerMessage) msg));
		CommandParserWrapper.addCommand("next_round", msg -> onNextRound((NextRoundMessage) msg));
		CommandParserWrapper.addCommand("next_round", msg -> uiManager.selectPlayer((NextRoundMessage) msg));
		CommandParserWrapper.addCommand("response", msg -> onServerResponse((ResponseMessage) msg));
		CommandParserWrapper.addCommand("move_player", msg -> onPlayerMoved((MovePlayerMessage) msg));
		CommandParserWrapper.addCommand("self_data", msg -> onSelfDataGiven((SelfDataMessage) msg));

		responseManager.addWaitingResponse("move_request");
	}


	/**
	 * @brief Adds a player to the game.
	 * @param id	Unique player ID.
	 * @param name	Non-empty player name.
	 */
	private void addPlayer(final int id, final String name)
	{
		if (name == null || name.isEmpty())
		{
			throw new IllegalArgumentException("Invalid player name.");
		}

		players.put(id, new Player(id, name));
		uiManager.addPlayer(id, name);
	}


	/**
	 * @brief Returns all players in the game.
	 */
	public Map<Integer, Player> getPlayers()
	{
		return players;
	}


	/**
	 * @brief Renders the game board, including all pawns.
	 * @param gc	Canvas graphics context.
	 * @param offsetX	Offset from the upper-left corner to the center of the board.
	 * @param offsetY	Offset from the upper-left corner to the center of the board.
	 */
	public void renderBoard(final GraphicsContext gc, final float offsetX, final float offsetY)
	{
		if (boardManager == null)
		{
			return;
		}

		boardManager.renderTiles(gc, offsetX, offsetY);
		this.renderPawns(gc, offsetX, offsetY);
	}


	/**
	 * @brief Renders all pawns on the board.
	 * @param gc	Canvas graphics context.
	 * @param offsetX	Offset from the upper-left corner to the center of the board.
	 * @param offsetY	Offset from the upper-left corner to the center of the board.
	 */
	private void renderPawns(final GraphicsContext gc, final float offsetX, final float offsetY)
	{
		players.forEach((id, player) -> {
			player.getPawns().forEach((pawnID, pawn) -> {
				pawn.draw(gc, 0, 0);
			});
		});
	}


	/**
	 * @brief Send a request to the server to join the game.
	 * @param name	Non-empty player name.
	 */
	public void requestJoin(final String name)
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


	/**
	 * @brief Locally moves a pawn to a new position on the board and awaits server response to confirm the move.
	 * @param pawn	Pawn to move.
	 * @param newPosition	New board position to move the pawn to.
	 */
	public void moveLocally(final Pawn pawn, final Position newPosition)
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


	/**
	 * @brief Send information to the server that the player has ended their turn.
	 */
	public void endTurn()
	{
		if (!NetworkConnector.isConnected())
		{
			System.out.println("Not connected to a server.");
			return;
		}

		Message msg = new EndTurnMessage();
		String json = msg.toJson();

		if (json == null)
		{
			System.out.println("Failed to create JSON message.");
			return;
		}

		NetworkConnector.send(json);
	}


	/**
	 * @brief Handles a server response.
	 * @param json	Server response message.
	 */
	private void onServerResponse(final ResponseMessage json)
	{
		if (json == null)
		{
			System.out.println("Invalid server response.");
			return;
		}

		responseManager.pushResponse(json);
	}



	/**
	 * @brief Handles a game start message from the server.
	 * @param json	Game start message.
	 */
	private void onGameStart(final GameStartMessage json)
	{
		int boardSize = json.getBoardSize();
		this.boardManager = new BoardManager(new DefaultBoard(new Point2D(uiManager.getCanvasSize().getX() / 2f, uiManager.getCanvasSize().getY() / 2f), 25, boardSize));

		String variant = json.getVariant();

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
		uiManager.addMessage("Variant: " + variant);
	}



	/**
	 * @brief Handles a game end message from the server.
	 * @param json	Game end message.
	 */
	private void onNextRound(final NextRoundMessage json)
	{
		currentPlayerID = json.getCurrentPlayerID();
	}

	/**
	 * @brief Handles a player move message from the server.
	 * @param json	Player move message.
	 */
	private void onPlayerMoved(final MovePlayerMessage json)
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


	private void onSelfDataGiven(final SelfDataMessage json)
	{
		myPlayerID = json.getPlayerID();
		PlayerData.id = myPlayerID;

		System.out.println("Updating data: ID=" + myPlayerID);
	}

	/**
	 * @brief Handles a winner announcement message from the server.
	 * @param json	Winner announcement message.
	 */
	private void onWinnerAnnounced(final AnnounceWinnerMessage json)
	{
		Player winner = players.get(json.getPlayerID());

		if (winner == null)
		{
			System.out.println("[ERROR]: winner does not exist.");
			return;
		}

		String numberEnding = switch (json.getPlayerID())
		{
			case 1 -> "st";
			case 2 -> "nd";
			case 3 -> "rd";
			default -> "th";
		};

		uiManager.addMessage("Player " + winner.getName() + " has taken " + json.getPlayerID() + numberEnding + " place.");

		if (winner.getID() == myPlayerID)
		{
			uiManager.disableUI(true);
		}
	}


	/**
	 * @brief Returns the board manager. Warning: board manager may be null.
	 */
	public BoardManager getBoardManager()
	{
		return boardManager;
	}
}
