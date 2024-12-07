package com.chinese_checkers.networking;

import java.util.HashMap;
import java.util.function.Consumer;

public class CommandParser
{
	private final HashMap<String, Consumer<String>> commands = new HashMap<>();

	public CommandParser()
	{

	}

	public void parseCommand(String json)
	{
		// for testing
		if (commands.containsKey(json))
		{
			commands.get(json).accept(json);
		}
		else
		{
			System.out.println("Command not found: " + json);
		}
	}

	public void addCommand(String command, Consumer<String> action)
	{
		commands.put(command, action);
	}

}
