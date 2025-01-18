package com.chinese_checkers.networking;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.comms.Message.Message;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The NetworkListener thread listens for incoming messages from the network and automatically processes them.
 */
public class NetworkListener extends Thread
{
	private final BufferedReader in;
	private final ReentrantLock threadLock;
	private boolean running = false;


	public NetworkListener(final BufferedReader in, final ReentrantLock threadLock)
	{
		this.in = in;
		this.threadLock = threadLock;
	}


	@Override
	public void run()
	{
		running = true;
		String line;

		try
		{
			Message message;

			while (running)
			{
				System.out.println("here");
				line = in.readLine();
				System.out.println("[debug] Received: " + line);

				threadLock.lock();
				// null or EOF
				if (line == null)
					break;

				message = Message.fromJson(line);
				if (message != null)
					CommandParserWrapper.parse(message);
				threadLock.unlock();
			}
		}
		catch (final IOException e)
		{
			// Socket closed
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
		System.out.println("Terminating listener");
		running = false;
	}

	public boolean isRunning()
	{
		return running;
	}
}
