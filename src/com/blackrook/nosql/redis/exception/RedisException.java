package com.blackrook.nosql.redis.exception;

/**
 * An exception thrown during a Redis command or a read error.
 * @author Matthew Tropiano
 */
public class RedisException extends RuntimeException
{
	private static final long serialVersionUID = -299077146531902487L;

	public RedisException()
	{
		super("An exception occurred.");
	}

	public RedisException(String message)
	{
		super(message);
	}

	public RedisException(Throwable cause)
	{
		super(cause);
	}
	
	public RedisException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
}
