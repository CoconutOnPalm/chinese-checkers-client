package com.chinese_checkers.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;
import com.chinese_checkers.comms.CommandParser;

public class NetworkConnector
{
	private final int port;
	private final String hostname;
	private Socket socket;

	private BufferedReader in;
	private PrintWriter out;

	private final ReentrantLock lock = new ReentrantLock();
	private NetworkListener listener;
	private final CommandParser commandParser;


	public NetworkConnector(final String hostname, final int port, final CommandParser commandParser)
	{
		this.port = port;
		this.hostname = hostname;
		this.commandParser = commandParser;
	}



	public boolean connect()
	{
		return connect(10, 1000);
	}


	/**
	 * @brief Connects to the server
	 * @param max_attempts              Maximum number of attempts to connect
	 * @param connection_frequency_ms   Frequency of connection attempts in milliseconds
	 */
	public boolean connect(final int max_attempts, final int connection_frequency_ms)
	{
		boolean success = false;
		for (int i = 0; i < max_attempts; i++)
		{
			try
			{
				socket = new Socket(hostname, port);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				success = true;
				break;
			}
			catch (final UnknownHostException e)
			{
				System.out.println("Unknown host or host did not accept: " + hostname + ". Attempt " + (i + 1) + "/" + max_attempts);
			}
			catch (final IOException e)
			{
				System.out.println("Server not available. Attempt " + (i + 1) + "/" + max_attempts);
			}
			catch (final SecurityException e)
			{
				System.out.println("Security error: " + e);
			}
			catch (final IllegalArgumentException e)
			{
				System.out.println("Invalid port number: " + port);
			}
			finally
			{
				try
				{
					Thread.sleep(connection_frequency_ms);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		if (!success)
		{
			System.out.println("Could not connect to server");
			return false;
		}

		listener = new NetworkListener(in, lock, commandParser);
		listener.start();

		return true;
	}

	/**
	 * @brief Disconnects from the server
	 */
	public void disconnect()
	{
		if (listener == null)
		{
			System.out.println("Listener not initialized");
			return;
		}

		listener.terminate();

		try
		{
			// force disconnect after 2 seconds
			listener.join(2 * 1000);
		} catch (InterruptedException e)
		{
			System.out.println("Listener thread interrupted");
		}

		try
		{
			if (socket != null)
				socket.close();
			if (in != null)
				in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * @brief Sends a message to the server
	 * @param message   Message to send
	 */
	public void send(String message)
	{
		out.println(message);
	}

	public String expectResponse(String type)
	{
		try
		{
			String line = in.readLine();
			if (line == null)
				return null;

			String[] parts = line.split(" ");
			if (parts.length < 2 || !parts[0].equals(type))
				return null;

			return line.substring(parts[0].length() + 1);
		}
		catch (IOException e)
		{
			System.out.println("I/O error: " + e);
			return null;
		}
	}

	public boolean isConnected()
	{
		return socket != null && listener.isRunning();
	}
}
