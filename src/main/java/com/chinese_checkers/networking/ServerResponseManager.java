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

public class ServerResponseManager
{
	private final Map<String, CompletableFuture<ResponseMessage>> responseMap = new HashMap<>();


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
		CompletableFuture<ResponseMessage> future = responseMap.get(command);
		if (future == null)
		{
			System.out.println("DEBUG: Command not found: " + command);
			return new ResponseMessage(command, ResponseMessage.Status.FAILURE, "future failed");
		}

		try {
			ResponseMessage msg = future.get(maxWaitTime_sec, TimeUnit.SECONDS);
			System.out.println("> [DEBUG]: Received response: " + msg.getToWhatAction() + " " + msg.getStatus() + " " + msg.getMessage() + " <");
			return msg;
		} catch (TimeoutException e) {
			System.out.println("[ERROR]: Timeout or interruption while waiting for response");
			return new ResponseMessage(command, ResponseMessage.Status.FAILURE, "timeout");
		} catch (CancellationException e) {
			System.out.println("[ERROR]: Future was cancelled");
			return new ResponseMessage(command, ResponseMessage.Status.FAILURE, "cancelled");
		} catch (Exception e) {
			System.out.println("[ERROR]: Exception while waiting for response: " + e.getMessage());
			return new ResponseMessage(command, ResponseMessage.Status.FAILURE, "exception");
		}
	}
}
