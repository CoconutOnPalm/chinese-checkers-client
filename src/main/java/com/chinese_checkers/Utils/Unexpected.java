package com.chinese_checkers.Utils;

public class Unexpected<T> extends Expected<T>
{
	public Unexpected(String message)
	{
		this.message = message;
		this.hasValue = false;
	}
}
