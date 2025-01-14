package com.chinese_checkers.game;

import com.chinese_checkers.Utils.Expected;
import com.chinese_checkers.Utils.Unexpected;
import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.FromClient.DisconnectMessage;
import com.chinese_checkers.comms.Message.FromClient.MoveRequestMessage;
import com.chinese_checkers.comms.Message.FromClient.RequestJoinMessage;
import com.chinese_checkers.comms.Message.FromServer.*;
import com.chinese_checkers.comms.Message.Message;
import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.networking.ServerResponseManager;
import com.chinese_checkers.ui.GlobalStageData;
import com.chinese_checkers.ui.UIManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Game
{
	private boolean isRunning = true;
	private NetworkConnector server = GlobalStageData.networkConnector;
	private final ServerResponseManager responseManager = new ServerResponseManager();
	private final UIManager uiManager;


	private int myPlayerID = -1;

	public Game(final UIManager uiManager)
	{
		this.uiManager = uiManager;

		CommandParser serverCommandParser = GlobalStageData.commandParser;
		serverCommandParser.addCommand("game_start", msg -> onGameStart((GameStartMessage) msg));
		serverCommandParser.addCommand("game_end", msg -> onGameEnd((GameEndMessage) msg));
		serverCommandParser.addCommand("next_round", msg -> onNextRound((NextRoundMessage) msg));
		serverCommandParser.addCommand("response", msg -> onServerResponse((ResponseMessage) msg));
		serverCommandParser.addCommand("move_player", msg -> onPlayerMoved((MovePlayerMessage) msg));
		serverCommandParser.addCommand("self_data", msg -> onSelfDataGiven((SelfDataMessage) msg));

		responseManager.addWaitingResponse("move_request");
	}


	public void exit()
	{
		System.out.println("Exiting game...");
	}


	public Expected<Boolean> connect(String hostname, int port)
	{
		if (server != null && server.isConnected())
		{
			System.out.println("Already connected to a server. Type 'disconnect' to disconnect.");
			return new Unexpected<>("Already connected to a server.");
		}

		server = new NetworkConnector(hostname, port);
		boolean status = false;//server.connect();

		if (!status)
		{
			server = null;
			return new Unexpected<>("Failed to connect to the server.");
		}
		else
		{
			return new Expected<>(true);
		}
	}

	public void connect(String line)
	{
		if (line == null || line.isEmpty())
		{
			System.out.println("Invalid connect command.");
			return;
		}

		String[] args = line.split(" ");

		if (args.length != 2)
		{
			System.out.println("Usage: connect <hostname> <port>");
			return;
		}

		String hostname = args[0];
		int port;

		try
		{
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e)
		{
			System.out.println("Invalid port number: " + args[1]);
			return;
		}

		if (server != null && server.isConnected())
		{
			System.out.println("Already connected to a server. Type 'disconnect' to disconnect.");
			return;
		}

		server = new NetworkConnector(hostname, port);
		boolean status = false;//server.connect();

		if (!status)
		{
			System.out.println("Failed to connect to the server.");
			server = null;
		}
		else
		{
			System.out.println("Connected to the server.");
		}
	}

	public void disconnect()
	{
		System.out.println("Disconnecting from the server...");

		if (server == null)
		{
			System.out.println("Not connected to a server.");
			return;
		}

		Message message = new DisconnectMessage();
		String json = message.toJson();

		if (json == null)
		{
			System.out.println("Failed to create JSON message.");
			return;
		}

		//server.send(json);

		// exits on server response
		isRunning = false;
		server.disconnect();
	}

	public void requestJoin(String name)
	{
		if (server == null)
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
		server.send(json);
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

		if (server == null)
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

		server.send(json);

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
		// Parse JSON and start the game

		System.out.println("Game started.");
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
}
