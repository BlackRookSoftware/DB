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
	/** Server connection timeout time. */
	private int timeout;
	
	/**
	 * Creates a Redis info object.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 */
	public RedisInfo(String host, int port)
	{
		this(host, port, null, 0);
	}
	
	/**
	 * Creates a Redis info object.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 */
	public RedisInfo(String host, int port, String password)
	{
		this(host, port, password, 0);
	}

	/**
	 * Creates a Redis info object.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param timeout the server socket connection timeout in milliseconds. 0 is no timeout.
	 */
	public RedisInfo(String host, int port, int timeout)
	{
		this(host, port, null, timeout);
	}
	
	/**
	 * Creates a Redis info object.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 * @param timeout the server socket connection timeout in milliseconds. 0 is no timeout.
	 */
	public RedisInfo(String host, int port, String password, int timeout)
	{
		this.host = host;
		this.port = port;
		this.password = password;
		this.timeout = timeout;
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
	
	/**
	 * Gets the server connection timeout in milliseconds.
	 * 0 is no timeout.
	 */
	public int getTimeout()
	{
		return timeout;
	}
	
}
