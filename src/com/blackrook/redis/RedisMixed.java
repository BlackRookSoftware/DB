package com.blackrook.redis;

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
	
}
