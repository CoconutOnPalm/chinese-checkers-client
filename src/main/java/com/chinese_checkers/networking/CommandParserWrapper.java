package com.chinese_checkers.networking;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.Message;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * The CommandParserWrapper class wraps the CommandParser class to provide a thread-safe singleton interface for adding
 * commands and parsing messages.
 */
public class CommandParserWrapper
{
	private final CommandParser commandParser;

	private static final ReentrantLock lock = new ReentrantLock();
	private static CommandParserWrapper instance;

	CommandParserWrapper()
	{
		commandParser = new CommandParser();
	}

	private static CommandParserWrapper getInstance()
	{
		if (instance == null)
			instance = new CommandParserWrapper();
		return instance;
	}

	/**
	 * @brief Registers a command with the command parser
	 *
	 * @param command see chinese_checkers.comms.Message for a list of possible commands
	 * @param handler callback function to handle the command
	 */
	public static void addCommand(final String command, final Consumer<Message> handler)
	{
		lock.lock();
		getInstance().commandParser.addCommand(command, handler);
		lock.unlock();
	}

	/**
	 * @brief Parses a message using the command parser
	 *
	 * @param message see chinese_checkers.comms.Message for a list of possible commands
	 */
	public static void parse(final Message message)
	{
		lock.lock();
		getInstance().commandParser.parseCommand(message);
		lock.unlock();
	}
}
