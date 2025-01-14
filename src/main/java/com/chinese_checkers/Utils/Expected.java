package com.chinese_checkers.Utils;

/**
 * @brief Simple class to mimic c++ std::expected
 */

public class Expected<T>
{
	protected T value;
	protected String message;
	protected boolean hasValue;

	public Expected()
	{
		this.message = null;
		this.hasValue = false;
	}

	public Expected(T value)
	{
		this.value = value;
		this.message = null;
		this.hasValue = true;
	}

	public Expected(T value, String message)
	{
		this.value = value;
		this.message = message;
		this.hasValue = true;
	}

	public boolean hasValue()
	{
		return hasValue;
	}

	public T getValue()
	{
		return value;
	}

	public String getMessage()
	{
		return message;
	}
}
