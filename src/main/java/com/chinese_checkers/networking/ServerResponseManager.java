package com.chinese_checkers.networking;

import com.chinese_checkers.Utils.Pair;
import com.chinese_checkers.comms.Message.FromServer.ResponseMessage;
import com.chinese_checkers.comms.Message.Message;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

public class ServerResponseManager
{
	private final Map<String, CompletableFuture<ResponseMessage>> responseMap = new HashMap<>();
	private static final ReentrantLock lock = new ReentrantLock();


	public void addWaitingResponse(String command)
	{
		responseMap.put(command, new CompletableFuture<>());
	}

	public void pushResponse(ResponseMessage response)
	{
		CompletableFuture<ResponseMessage> future = responseMap.get(response.getToWhatAction());
		if (future == null)
		{
			//System.out.println("DEBUG: Command not found: " + response.getToWhatAction());
			return;
		}
		future.complete(response);
	}

	public ResponseMessage waitForResponse(String command, int maxWaitTime_sec)
	{
		//lock.lock();
		CompletableFuture<ResponseMessage> future = responseMap.get(command);
		if (future == null)
		{
			System.out.println("DEBUG: Command not found: " + command);
			return new ResponseMessage(command, ResponseMessage.Status.FAILURE, "future failed");
		}

		try {
			ResponseMessage msg = future.get(maxWaitTime_sec, TimeUnit.SECONDS);
			responseMap.replace(command, new CompletableFuture<>());
			System.out.println("> [DEBUG]: Received response: " + msg.getToWhatAction() + " " + msg.getStatus() + " " + msg.getMessage() + " <");
			//lock.unlock();
			return msg;
		} catch (TimeoutException e) {
			System.out.println("[ERROR]: Timeout or interruption while waiting for response");
			//lock.unlock();
			return new ResponseMessage(command, ResponseMessage.Status.FAILURE, "timeout");
		} catch (CancellationException e) {
			System.out.println("[ERROR]: Future was cancelled");
			//lock.unlock();
			return new ResponseMessage(command, ResponseMessage.Status.FAILURE, "cancelled");
		} catch (Exception e) {
			System.out.println("[ERROR]: Exception while waiting for response: " + e.getMessage());
			//lock.unlock();
			return new ResponseMessage(command, ResponseMessage.Status.FAILURE, "exception");
		}
	}
}
