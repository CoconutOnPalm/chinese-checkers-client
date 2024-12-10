package com.chinese_checkers.networking;

import com.chinese_checkers.Utils.Pair;
import com.chinese_checkers.comms.Message.FromServer.ResponseMessage;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ServerResponseManager
{
	private final Map<String, CompletableFuture<String>> responseMap = new HashMap<>();


	public void addWaitingResponse(String command)
	{
		responseMap.put(command, new CompletableFuture<>());
	}

	public void pushResponse(ResponseMessage response)
	{
		CompletableFuture<String> future = responseMap.get(response.getToWhatAction());
		if (future == null)
		{
			//System.out.println("DEBUG: Command not found: " + response.getToWhatAction());
			return;
		}
		future.complete(response.getMessage());
	}

	public String waitForResponse(String command, int maxWaitTime)
	{
		CompletableFuture<String> future = responseMap.get(command);
		if (future == null)
		{
			//System.out.println("DEBUG: Command not found: " + command);
			return "error";
		}

		try {
			return future.get(maxWaitTime, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.out.println("[ERROR]: Timeout or interruption while waiting for response");
			return "timeout";
		}
	}
}
