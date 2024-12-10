package com.chinese_checkers.game;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.FromClient.DisconnectMessage;
import com.chinese_checkers.comms.Message.FromClient.MoveRequestMessage;
import com.chinese_checkers.comms.Message.FromClient.RequestJoinMessage;
import com.chinese_checkers.comms.Message.FromServer.*;
import com.chinese_checkers.comms.Message.Message;
import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.networking.ServerResponseManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Game
{
	private boolean isRunning = true;
	private final CommandParser serverCommandParser = new CommandParser();
	private final ClientCommandParser clientCommandParser = new ClientCommandParser();
	private NetworkConnector server = null;
	private final ServerResponseManager responseManager = new ServerResponseManager();


	private final HashMap<Integer, Player> players = new HashMap<>(); // Key: player ID, Value: Player object
	private int myPlayerID = -1;

	public Game()
	{
		serverCommandParser.addCommand("game_start", msg -> onGameStart((GameStartMessage) msg));
		serverCommandParser.addCommand("game_end", msg -> onGameEnd((GameEndMessage) msg));
		serverCommandParser.addCommand("next_round", msg -> onNextRound((NextRoundMessage) msg));
		serverCommandParser.addCommand("response", msg -> onServerResponse((ResponseMessage) msg));
		serverCommandParser.addCommand("move_player", msg -> onPlayerMoved((MovePlayerMessage) msg));
		serverCommandParser.addCommand("self_data", msg -> onSelfDataGiven((SelfDataMessage) msg));

		clientCommandParser.addCommand("connect", this::connect);
		clientCommandParser.addCommand("disconnect", this::disconnect);
		clientCommandParser.addCommand("exit", this::disconnect); // alias
		clientCommandParser.addCommand("join", this::requestJoin);
		clientCommandParser.addCommand("move", this::moveLocally);

		responseManager.addWaitingResponse("move_request");
	}

	public void run()
	{
		Scanner scanner = new Scanner(System.in);

		// force connecting first
		System.out.println("Chinese Checkers Game");
		System.out.println("Type 'connect <hostname> <port>' to connect to the server.");

		while (isRunning)
		{
			System.out.print("> ");
			String line = scanner.nextLine();
			clientCommandParser.parseCommand(line);
		}

		System.out.println("Exiting game...");

		scanner.close();
		server.disconnect();
	}

	private void connect(String line)
	{
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

		server = new NetworkConnector(hostname, port, serverCommandParser);
		boolean status = server.connect();

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

	private void disconnect(String line)
	{
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

		server.send(json);

		// exits on server response
		isRunning = false;
		server.disconnect();
	}

	private void requestJoin(String name)
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

		server.send(json);
	}


	private void moveLocally(String line)
	{
		// Usage: move <pawn> <s> <q> <r>
		String[] args = line.split(" ");

		if (args.length != 4)
		{
			System.out.println("Usage: move <pawn> <s> <q> <r>");
			return;
		}

		int pawn;
		int s, q, r;

		try
		{
			pawn = Integer.parseInt(args[0]);
			s = Integer.parseInt(args[1]);
			q = Integer.parseInt(args[2]);
			r = Integer.parseInt(args[3]);
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

		Message msg = new MoveRequestMessage(pawn, s, q, r);
		String json = msg.toJson();

		if (json == null)
		{
			System.out.println("Failed to create JSON message.");
			return;
		}

		server.send(json);

		// await server response
		String status = responseManager.waitForResponse("move_request", 5000);

		if (status == null || !status.equals("success"))
		{
			System.out.println("Move failed.");
			// TODO: revert move
			return;
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
		int s = json.s;
		int q = json.q;
		int r = json.r;

		if (playerID == myPlayerID)
		{
			// check if this pawn is at the correct position
			// if not, do what the server says
			return;
		}

		System.out.println("Moving player ID=" + playerID + ": pawn ID=" + pawnID + " to (" + s + ", " + q + ", " + r + ")");
	}

	private void onSelfDataGiven(SelfDataMessage json)
	{
		myPlayerID = json.getPlayerID();

		System.out.println("Updating data: ID=" + myPlayerID);
	}
}
