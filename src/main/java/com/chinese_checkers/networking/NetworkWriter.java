package com.chinese_checkers.networking;

import java.io.PrintWriter;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkWriter
{
	private PrintWriter out;
	private ReentrantLock threadLock;

	public NetworkWriter(final PrintWriter out, final ReentrantLock threadLock)
	{
		this.out = out;
		this.threadLock = threadLock;
	}

	public void write(String message)
	{
		threadLock.lock();
		out.println(message + "\n");
		threadLock.unlock();
	}
}
