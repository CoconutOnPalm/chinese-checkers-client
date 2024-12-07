package com.chinese_checkers.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkConnector
{
	private final int port;
	private final String hostname;
	private Socket socket;

	private BufferedReader in;
	private PrintWriter out;

	private ReentrantLock lock = new ReentrantLock();
	private NetworkListener listener;



	public NetworkConnector(int port, String hostname)
	{
		this.port = port;
		this.hostname = hostname;
	}


	public void connect()
	{
		connect(10, 1000);
	}


	public void connect(final int max_attempts, final int connection_frequency_ms)
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
				System.out.println("Successfully connected to server: " + hostname + ":" + port);
				break;
			}
			catch (final UnknownHostException e)
			{
				System.out.println("Unknown host: " + hostname);
			}
			catch (final IOException e)
			{
				System.out.println("I/O error: " + e);
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
			return;
		}

		listener = new NetworkListener(in, lock);
		listener.start();
	}

	public void disconnect()
	{
		try
		{
			listener.join();
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


	public void send(String message)
	{
		out.println(message);
	}
}
