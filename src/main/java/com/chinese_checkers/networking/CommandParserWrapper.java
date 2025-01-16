package com.chinese_checkers.networking;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.Message;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

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

	public static void addCommand(String command, Consumer<Message> handler)
	{
		lock.lock();
		getInstance().commandParser.addCommand(command, handler);
		lock.unlock();
	}

	public static void parse(Message message)
	{
		lock.lock();
		getInstance().commandParser.parseCommand(message);
		lock.unlock();
	}
}
