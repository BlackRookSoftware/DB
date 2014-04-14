package com.blackrook.nosql.redis;

import java.util.Arrays;

import com.blackrook.commons.Common;

/**
 * A "mixed" type for unexpected reads from a server response.
 * @author Matthew Tropiano
 */
public class RedisMixed
{
	public static enum Type
	{
		INTEGER,
		STRING,
		ARRAY,
		ERROR;
	}
	
	private Type type;
	private String value;
	private String[] values;
	
	/**
	 * Creates a numeric response.
	 */
	public RedisMixed(long lng)
	{
		this.type = Type.INTEGER;
		this.value = String.valueOf(lng); 
	}

	/**
	 * Creates a string response.
	 */
	public RedisMixed(String str)
	{
		this(str, false);
	}
	
	/**
	 * Creates a string response.
	 */
	public RedisMixed(String str, boolean error)
	{
		if (error)
		{
			this.type = Type.ERROR;
			this.value = str;
		}
		else
		{
			this.type = Type.STRING;
			this.value = str;
		}
	}
	
	/**
	 * Creates a string array response.
	 */
	public RedisMixed(String ...str)
	{
		this.type = Type.ARRAY;
		this.values = str;
	}

	/**
	 * Returns the internal type of this mixed value.
	 */
	public Type getType()
	{
		return type;
	}
	
	/**
	 * Returns this value as a Long integer. Can return null.
	 * If the internal type is {@link Type#ERROR}, this returns null.
	 */
	public Long getInteger()
	{
		switch (type)
		{
			case INTEGER:
			case STRING:
				return value != null ? Common.parseLong(value) : null;
			case ARRAY:
				return values != null && values.length > 0 ? Common.parseLong(values[0]) : null;
			default:
			case ERROR:
				return null;
		}
	}
	
	/**
	 * Returns this value as a String. Can return null.
	 * If the internal type is {@link Type#ERROR}, this returns null.
	 */
	public String getString()
	{
		switch (type)
		{
			case INTEGER:
			case STRING:
				return value;
			case ARRAY:
				return values != null && values.length > 0 ? values[0] : null;
			default:
			case ERROR:
				return null;
		}
	}
	
	/**
	 * Returns this value as a String Array. Can return null.
	 * If the internal type is {@link Type#ERROR}, this returns null.
	 */
	public String[] getStringArray()
	{
		switch (type)
		{
			case INTEGER:
			case STRING:
				return value != null ? new String[]{value} : null;
			case ARRAY:
				return values;
			default:
			case ERROR:
				return null;
		}
	}

	/**
	 * Returns this value as an error String. Can return null.
	 * If the internal type is NOT {@link Type#ERROR}, this returns null.
	 */
	public String getError()
	{
		switch (type)
		{
			case INTEGER:
			case STRING:
			case ARRAY:
			default:
				return null;
			case ERROR:
				return value;
		}
	}
	
	@Override
	public String toString()
	{
		switch (type)
		{
			case INTEGER:
			case STRING:
			case ERROR:
				return String.valueOf(value);
			case ARRAY:
				return Arrays.toString(values);
			default:
				return null;
		}
	}
	
}
