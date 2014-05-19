/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis;

import java.io.IOException;
import java.net.UnknownHostException;

import com.blackrook.commons.hash.Hash;
import com.blackrook.commons.linkedlist.Queue;

/**
 * A connection pool for Redis socket connections.
 * Connections are fair - released connections are added to the end of an "available" queue.
 * @author Matthew Tropiano
 */
public class RedisConnectionPool
{
	/** Available connections. */
	private Queue<RedisConnection> availableConnections;
	/** Used connections. */
	private Hash<RedisConnection> usedConnections;
	
	/** Primary info. */
	private RedisInfo info;
	
	// private constructor.
	private RedisConnectionPool()
	{
		availableConnections = new Queue<RedisConnection>();
		usedConnections = new Hash<RedisConnection>();
	}
	
	/**
	 * Creates a connection pool using a connection to a host.
	 * @param connections the number of connections to open.
	 * @param host the host to connect to.
	 * @param port the port to connect to on the host.
	 * @param password the Redis DB password.
	 * @throws IOException if a connection can't be made.
	 * @throws UnknownHostException if the server host can't be resolved.
	 */
	public RedisConnectionPool(int connections, String host, int port, String password) throws IOException
	{
		this(connections, new RedisInfo(host, port, password));
	}
	
	/**
	 * Creates a connection pool using a connection to a host.
	 * @param connections the number of connections to open.
	 * @param info the {@link RedisInfo} object to use to describe DB information.
	 * @throws IOException if a connection can't be made.
	 * @throws UnknownHostException if the server host can't be resolved.
	 * @since 2.3.0
	 */
	public RedisConnectionPool(int connections, RedisInfo info) throws IOException
	{
		this();
		for (int i = 0; i < connections; i++)
			availableConnections.add(new RedisConnection(info));
	}
	
	/**
	 * Attempts to return an available connection.
	 * Will block until one becomes available.
	 * ALWAYS RELEASE FINISHED CONNECTIONS!!!!
	 * @return an available connection.
	 */
	public RedisConnection getConnection()
	{
		RedisConnection out = null;
		
		synchronized (availableConnections)
		{
			while (availableConnections.isEmpty())
			{
				try {
					availableConnections.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException("Broke out of wait() in "+this.getClass().getName());
				}
			}
			
			out = availableConnections.dequeue();
		}
		
		synchronized (usedConnections)
		{
			usedConnections.put(out);
		}
		
		return out;
	}

	/**
	 * Releases a Redis connection.
	 * @param connection the connection to release.
	 */
	public void releaseConnection(RedisConnection connection)
	{
		if (!usedConnections.contains(connection))
			throw new IllegalStateException("Connection was not acquired!");
		
		synchronized (usedConnections)
		{
			usedConnections.remove(connection);
		}
		synchronized (availableConnections)
		{
			availableConnections.enqueue(connection);
			availableConnections.notifyAll();
		}
	}
	
	/**
	 * Returns the amount of available connections.
	 */
	public int getAvailableConnectionCount()
	{
		return availableConnections.size();
	}
	
	/**
	 * Returns the amount of used connections.
	 */
	public int getUsedConnectionCount()
	{
		return usedConnections.size();
	}

}
