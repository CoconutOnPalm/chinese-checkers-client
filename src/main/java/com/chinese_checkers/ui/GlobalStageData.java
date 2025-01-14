package com.chinese_checkers.ui;

import com.chinese_checkers.comms.CommandParser;
import com.chinese_checkers.networking.NetworkConnector;

public record GlobalStageData()
{
	public static NetworkConnector networkConnector;
	public static CommandParser commandParser;
	public static String username;
}
