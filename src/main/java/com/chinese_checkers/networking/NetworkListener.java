package com.chinese_checkers.networking;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.Message;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkListener extends Thread
{
	private final BufferedReader in;
	private final ReentrantLock threadLock;
	private final CommandParser commandParser;
	private boolean running = false;


	public NetworkListener(final BufferedReader in, final ReentrantLock threadLock, final CommandParser commandParser)
	{
		this.in = in;
		this.threadLock = threadLock;
		this.commandParser = commandParser;
	}


	@Override
	public void run()
	{
		running = true;
		String line;

		try
		{
			line = in.readLine();

			if (line != null)
			{
				threadLock.lock();
				Message message = Message.fromJson(line);
				if (message != null)
					commandParser.parseCommand(message);
				threadLock.unlock();

				while (running)
				{
					line = in.readLine();

					threadLock.lock();
					if (line == null)
						break;

					message = Message.fromJson(line);
					if (message != null)
						commandParser.parseCommand(message);
					threadLock.unlock();
				}
			}
		}
		catch (IOException e)
		{
			System.out.println("I/O error: " + e);
		}
		finally
		{
			if (threadLock.isHeldByCurrentThread())
				threadLock.unlock();
			running = false;
		}

		System.out.println("Connection closed");
	}

	public void terminate()
	{
		running = false;
	}

	public boolean isRunning()
	{
		return running;
	}
}
