package com.chinese_checkers.game;

import java.util.HashMap;
import java.util.function.Consumer;

public class ClientCommandParser
{
	private final HashMap<String, Consumer<String>> commands = new HashMap<>();


	public ClientCommandParser()
	{
	}

	public void parseCommand(String line)
	{
		if (line == null)
		{
			System.out.println("Invalid command");
			return;
		}

		if (line.isEmpty())
		{
			return;
		}

		String[] tokens = line.split(" ");

		if (tokens.length == 0)
		{
			System.out.println("Invalid command");
			return;
		}

		String command = tokens[0];
		String args = null;

		if (tokens.length > 1)
		{
			args = line.substring(command.length() + 1);
		}

		if (commands.containsKey(command))
		{
			commands.get(command).accept(args);
		}
		else
		{
			System.out.println("Command not found: " + command);
		}
	}


	public void addCommand(String command, Consumer<String> action)
	{
		commands.put(command, action);
	}
}
