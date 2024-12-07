package com.chinese_checkers.game;

import java.util.HashMap;
import java.util.function.Consumer;

public class UserCommandParser
{
	private final HashMap<String, Consumer<String>> commands = new HashMap<>();


	public UserCommandParser()
	{
	}

	public void parseCommand(String line)
	{
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
