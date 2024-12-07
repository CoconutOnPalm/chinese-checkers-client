package com.chinese_checkers.networking;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkListener extends Thread
{
	private final BufferedReader in;
	private final ReentrantLock threadLock;
	private final CommandParser commandParser;
	private boolean running = true;


	public NetworkListener(final BufferedReader in, final ReentrantLock threadLock, final CommandParser commandParser)
	{
		this.in = in;
		this.threadLock = threadLock;
		this.commandParser = commandParser;
	}


	@Override
	public void run()
	{
		String line;

		try
		{
			line = in.readLine();

			if (line != null)
			{
				threadLock.lock();
				commandParser.parseCommand(line);
				threadLock.unlock();

				while (running)
				{
					line = in.readLine();

					threadLock.lock();
					if (line == null)
						break;

					commandParser.parseCommand(line);
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
		}

		System.out.println("Connection closed");
	}

	public void terminate()
	{
		running = false;
	}
}
