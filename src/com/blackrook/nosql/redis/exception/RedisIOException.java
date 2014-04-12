package com.blackrook.nosql.redis.exception;

/**
 * An exception thrown during a Redis stream read error.
 * @author Matthew Tropiano
 */
public class RedisIOException extends RuntimeException
{
	private static final long serialVersionUID = -299077146531902487L;

	public RedisIOException()
	{
		super("An exception occurred.");
	}

	public RedisIOException(String message)
	{
		super(message);
	}

	public RedisIOException(Throwable cause)
	{
		super(cause);
	}
	
	public RedisIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
}
