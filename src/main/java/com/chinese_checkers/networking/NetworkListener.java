package com.chinese_checkers.networking;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkListener extends Thread
{
	private final BufferedReader in;
	private final ReentrantLock threadLock;


	public NetworkListener(final BufferedReader in, final ReentrantLock threadLock)
	{
		this.in = in;
		this.threadLock = threadLock;
	}


	@Override
	public void run()
	{
		String line;

		try
		{
			threadLock.lock();
			line = in.readLine();
			threadLock.unlock();

			if (line != null)
			{
				CommandParser.parseCommand(line);

				while (true)
				{
					threadLock.lock();
					line = in.readLine();
					threadLock.unlock();

					if (line == null)
						break;

					CommandParser.parseCommand(line);
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
}
