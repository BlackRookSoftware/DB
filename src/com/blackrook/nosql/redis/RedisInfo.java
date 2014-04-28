/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
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
	/** Redis Database ID. */
	private int db;
	
	/**
	 * Creates a Redis info object, no timeout, nor password.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 */
	public RedisInfo(String host, int port)
	{
		this(host, port, null, 0, 0);
	}
	
	/**
	 * Creates a Redis info object, no timeout.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 */
	public RedisInfo(String host, int port, String password)
	{
		this(host, port, password, 0, 0);
	}

	/**
	 * Creates a Redis info object, no timeout, nor password.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 */
	public RedisInfo(String host, int port, int db)
	{
		this(host, port, null, 0, 0);
	}
	
	/**
	 * Creates a Redis info object, no timeout.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 */
	public RedisInfo(String host, int port, String password, int db)
	{
		this(host, port, password, 0, 0);
	}

	/**
	 * Creates a Redis info object, no password.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param timeout the server socket connection timeout in milliseconds. 0 is no timeout.
	 */
	public RedisInfo(String host, int port, int db, int timeout)
	{
		this(host, port, null, db, timeout);
	}
	
	/**
	 * Creates a Redis info object.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 * @param db the database to use for this connection.
	 * @param timeout the server socket connection timeout in milliseconds. 0 is no timeout.
	 */
	public RedisInfo(String host, int port, String password, int db, int timeout)
	{
		this.host = host;
		this.port = port;
		this.password = password;
		this.db = db;
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
	
	/**
	 * Gets the database id for the connection to use.
	 */
	public int getDB()
	{
		return db;
	}
	
}
