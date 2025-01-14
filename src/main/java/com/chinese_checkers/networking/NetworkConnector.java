package com.chinese_checkers.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

import com.chinese_checkers.Utils.Expected;
import com.chinese_checkers.Utils.Unexpected;
import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.ui.GlobalStageData;

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


	public NetworkConnector(final String hostname, final int port)
	{
		this.port = port;
		this.hostname = hostname;
		this.commandParser = GlobalStageData.commandParser;
	}



	public Expected<Boolean> connect()
	{
		return connect(10, 1000);
	}


	/**
	 * @brief Connects to the server
	 * @param max_attempts              Maximum number of attempts to connect
	 * @param connection_frequency_ms   Frequency of connection attempts in milliseconds
	 */
	public Expected<Boolean> connect(final int max_attempts, final int connection_frequency_ms)
	{
		boolean success = false;
		String lastError = null;

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
					Thread.sleep(connection_frequency_ms);
					System.out.println("Connection attempt " + i + " failed: " + lastError);
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
			return new Unexpected<>(lastError);
		}

		listener = new NetworkListener(in, lock, commandParser);
		listener.start();

		return new Expected<>(true);
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

		try
		{
			//socket.sendUrgentData(0xFF);
			//socket.shutdownInput();
			socket.close();
			in.close();
		} catch (IOException e)
		{
			System.out.println("Error shutting down input: " + e);
		}
		listener.terminate();

		try
		{
			//listener.join();
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
