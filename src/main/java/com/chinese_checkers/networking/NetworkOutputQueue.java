package com.chinese_checkers.networking;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkOutputQueue
{

	private final Queue<String> outputQueue;
	private final ReentrantLock threadLock;

	public NetworkOutputQueue(ReentrantLock threadLock)
	{
		outputQueue = new LinkedList<>(); // bruh
		this.threadLock = threadLock;
	}

	public void addMessage(String message)
	{
		threadLock.lock();
		outputQueue.add(message);
		threadLock.unlock();
	}
}
