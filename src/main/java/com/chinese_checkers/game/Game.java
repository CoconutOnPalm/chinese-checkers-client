package com.chinese_checkers.game;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.FromClient.RequestJoinMessage;
import com.chinese_checkers.comms.Message.FromServer.GameEndMessage;
import com.chinese_checkers.comms.Message.FromServer.GameStartMessage;
import com.chinese_checkers.comms.Message.FromServer.NextRoundMessage;
import com.chinese_checkers.comms.Message.FromServer.ResponseMessage;
import com.chinese_checkers.comms.Message.Message;
import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.comms.Message.Message.*;

import java.util.HashMap;
import java.util.Scanner;

public class Game
{
	private boolean isRunning = true;
	private final CommandParser serverCommandParser = new CommandParser();
	private final ClientCommandParser clientCommandParser = new ClientCommandParser();
	private NetworkConnector server = null;
	private final HashMap<Integer, Player> players = new HashMap<>(); // Key: player ID, Value: Player object

	public Game()
	{
		// commends are responsible for writing responses to the server
		serverCommandParser.addCommand("game_start", msg -> onGameStart((GameStartMessage) msg));
		serverCommandParser.addCommand("game_end", msg -> onGameEnd((GameEndMessage) msg));
		serverCommandParser.addCommand("next_round", msg -> onNextRound((NextRoundMessage) msg));
		serverCommandParser.addCommand("response", msg -> onServerResponse((ResponseMessage) msg));


		clientCommandParser.addCommand("connect", this::connect);
		clientCommandParser.addCommand("disconnect", this::disconnect);
		clientCommandParser.addCommand("join", this::requestJoin);
	}

	public void run()
	{
		Scanner scanner = new Scanner(System.in);

		// force connecting first
		System.out.println("Chinese Checkers Game");
		System.out.println("Type 'connect <hostname> <port>' to connect to the server.");

		while (isRunning)
		{
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

		if (server != null)
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

		server.send("disconnect");
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


	private void onServerResponse(ResponseMessage json)
	{
		// Parse JSON and handle server response



		System.out.println("Server response: " + json);
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

	private void initializePlayers(String json)
	{
		// Parse JSON and initialize players

		System.out.println("Initializing players...");
	}

	private void fetchBoard(String json)
	{
		// Parse JSON and update board

		System.out.println("Fetching board...");
	}

	private void movePlayer(String json)
	{
		// Parse JSON and move player

		System.out.println("Moving player...");
	}
}
