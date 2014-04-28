package com.blackrook.nosql.redis.exception;

/**
 * An exception thrown during a Redis stream read error.
 * @author Matthew Tropiano
 */
public class RedisParseException extends RuntimeException
{
	private static final long serialVersionUID = -299077146531902487L;

	public RedisParseException()
	{
		super("An exception occurred.");
	}

	public RedisParseException(String message)
	{
		super(message);
	}

	public RedisParseException(Throwable cause)
	{
		super(cause);
	}
	
	public RedisParseException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
}
