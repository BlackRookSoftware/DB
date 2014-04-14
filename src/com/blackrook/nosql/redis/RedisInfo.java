package com.blackrook.nosql.redis;

/**
 * Contains info about a single Redis server.
 * @author Matthew Tropiano
 */
public class RedisInfo
{
	/** Server hostname or address. */
	private String host;
	/** Server connection port. */
	private int port;
	/** Server database password. */
	private String password;
	
	/**
	 * Creates a Redis info object.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 */
	public RedisInfo(String host, int port)
	{
		this(host, port, null);
	}
	
	/**
	 * Creates a Redis info object.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 */
	public RedisInfo(String host, int port, String password)
	{
		this.host = host;
		this.port = port;
		this.password = password;
	}

	/**
	 * Gets the server hostname or address
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Gets the server connection port.
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Gets the server database password.
	 * If null, no password is submitted.
	 */
	public String getPassword()
	{
		return password;
	}
	
}
