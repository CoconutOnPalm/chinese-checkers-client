package com.chinese_checkers.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.ui.PlayerData;

/**
 * NetworkConnector class that handles the connection to the server. Singleton, thread-safe.
 */
public class NetworkConnector
{
	private Socket socket;

	private BufferedReader in;
	private PrintWriter out;

	private NetworkListener listener;

	private boolean killed = false;

	private static final ReentrantLock lock = new ReentrantLock();
	private static NetworkConnector instance = null;


	NetworkConnector()
	{
	}


	private static NetworkConnector getInstance()
	{
		if (instance == null)
			instance = new NetworkConnector();
		return instance;
	}


	/**
	 * Connects to the server (10 max attempts, 1000ms frequency)
	 */
	public static boolean connect(final String hostname, final int port) throws ConnectException
	{
		return connect(hostname, port, 10, 1000);
	}


	/**
	 * Connects to the server
	 * @param max_attempts              Maximum number of attempts to connect
	 * @param connection_frequency_ms   Frequency of connection attempts in milliseconds
	 */
	public static boolean connect(final String hostname, final int port, final int max_attempts, final int connection_frequency_ms) throws ConnectException
	{
		boolean success = false;
		String lastError = null;

		for (int i = 0; i < max_attempts && !getInstance().killed; i++)
		{
			try
			{
				getInstance().socket = new Socket(hostname, port);
				getInstance().in = new BufferedReader(new InputStreamReader(getInstance().socket.getInputStream()));
				getInstance().out = new PrintWriter(getInstance().socket.getOutputStream(), true);
				success = true;
				break;
			}
			catch (final UnknownHostException e)
			{
				lastError = "Unknown host: " + hostname;
			}
			catch (final IOException e)
			{
				lastError = "Server not responding";
			}
			catch (final SecurityException e)
			{
				lastError = "Security error: ";
			}
			catch (final IllegalArgumentException e)
			{
				lastError = "Invalid port number: " + port;
			}
			catch (final Exception e)
			{
				lastError = "Unknown error: " + e;
			}
			finally
			{
				try
				{
					if (!success)
					{
						Thread.sleep(connection_frequency_ms);
						System.out.println("Connection attempt " + i + " failed: " + lastError);
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		if (getInstance().killed)
		{
			System.out.println("Connection killed");
			throw new ConnectException("Connection killed");
		}

		if (!success)
		{
			System.out.println("Could not connect to server");
			throw new ConnectException("Could not connect to server");
		}

		getInstance();
		getInstance().listener = new NetworkListener(getInstance().in, lock);
		getInstance().listener.start();

		return true;
	}

	/**
	 * Disconnects from the server
	 */
	public static void disconnect()
	{
		var listener = getInstance().listener;

		if (listener == null)
		{
			getInstance().killed = true;
			return;
		}

		try
		{
			getInstance().socket.close();
			getInstance().in.close();
		} catch (final IOException e)
		{
			System.out.println("Error shutting down input: " + e);
		}
		listener.terminate();

		try
		{
			// force disconnect after 2 seconds
			listener.join(2 * 1000);
		} catch (final InterruptedException e)
		{
			System.out.println("Listener thread interrupted");
		}
	}


	/**
	 * Sends a message to the server
	 * @param message   Message to send
	 */
	public static void send(final String message)
	{
		lock.lock();
		getInstance().out.println(message);
		lock.unlock();
	}

	public String expectResponse(final String type)
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
		catch (final IOException e)
		{
			System.out.println("I/O error: " + e);
			return null;
		}
	}

	/**
	 * Checks if the client is connected to the server
	 */
	public static boolean isConnected()
	{
		return getInstance().socket != null && getInstance().listener.isRunning();
	}
}
