package com.chinese_checkers.game;

import com.chinese_checkers.networking.CommandParser;
import com.chinese_checkers.networking.NetworkConnector;

import java.util.HashMap;
import java.util.Scanner;

public class Game
{
	private boolean isRunning = true;
	private final CommandParser serverCommandParser = new CommandParser();
	private final UserCommandParser clientCommandParser = new UserCommandParser();
	private NetworkConnector server = null;
	private final HashMap<Integer, Player> players = new HashMap<>(); // Key: player ID, Value: Player object

	public Game()
	{
		// commends are responsible for writing responses to the server
		serverCommandParser.addCommand("join_request", this::onJoinRequestResponse);
		serverCommandParser.addCommand("game_start", this::initializePlayers);
		serverCommandParser.addCommand("game_end", this::onGameEnd);
		serverCommandParser.addCommand("fetch_players", this::initializePlayers);
		serverCommandParser.addCommand("fetch_board", this::fetchBoard);
		serverCommandParser.addCommand("move_player", this::movePlayer);


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
		server.connect();
	}

	private void disconnect(String line)
	{
		server.send("disconnect");
		isRunning = false;
		server.disconnect();
	}

	private void requestJoin(String name)
	{
		// Send request to the server to join the game
		server.send("join_game " + name);
	}

	private void onJoinRequestResponse(String json)
	{
		if (json.equals("join_request SUCCESS"))
		{
			System.out.println("Successfully joined the game.");
		}
		else if (json.equals("join_request FAILED"))
		{
			System.out.println("Failed to join the game.");
		}
	}

	private void onGameEnd(String json)
	{
		// Parse JSON and end the game

		System.out.println("Game ended.");
		isRunning = false;
		server.send("game_end SUCCESS");
	}

	private void initializePlayers(String json)
	{
		// Parse JSON and initialize players

		System.out.println("Initializing players...");

		server.send("fetch_board SUCCESS");
	}

	private void fetchBoard(String json)
	{
		// Parse JSON and update board

		System.out.println("Fetching board...");

		server.send("fetch_board SUCCESS");
	}

	private void movePlayer(String json)
	{
		// Parse JSON and move player

		System.out.println("Moving player...");

		server.send("move_player SUCCESS");
	}
}
