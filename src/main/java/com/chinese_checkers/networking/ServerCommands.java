package com.chinese_checkers.networking;

public enum ServerCommands
{

	GAME_START("game_start"),
	GAME_END("game_end"),
	NEXT_ROUND("next_round"),
	RESPONSE("response"),
	MOVE_PLAYER("move_player"),
	SELF_DATA("self_data"),
	DISCONNECT("disconnect"),
	REQUEST_JOIN("request_join"),
	REQUEST_REFRESH("request_refresh"),
	MOVE_REQUEST("move_request");



	private final String commandStr;

	ServerCommands(String commandStr)
	{
		this.commandStr = commandStr;
	}

	public String getCommandStr()
	{
		return commandStr;
	}
}
