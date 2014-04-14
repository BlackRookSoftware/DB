package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis connection stuff.
 * @author Matthew Tropiano
 */
public interface RedisConnectionCommands
{
	/** 
	 *	Authenticate to the server
	 *	connection
	 */
	public void auth();

	/** 
	 *	Echo the given string
	 *	connection
	 */
	public void echo();

	/** 
	 *	Ping the server
	 *	connection
	 */
	public void ping();

	/** 
	 *	Close the connection
	 *	connection
	 */
	public void quit();

	/** 
	 *	Change the selected database for the current connection
	 *	connection
	 */
	public void select();

}
