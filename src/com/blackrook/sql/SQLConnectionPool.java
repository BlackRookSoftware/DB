/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.sql.SQLTransaction.Level;

/**
 * This is a database connection pool class for a bunch of shared, managed connections.
 * Meant to be accessed by many threads in an enterprise setting.
 * If a connection is requested that is not available, the requesting thread will wait
 * until a connection is found or until it times out. 
 * @author Matthew Tropiano
 */
public class SQLConnectionPool
{
	/** Util reference. */
	protected SQLConnector utilRef;
	/** Pool username. */
	protected String userName;
	/** Pool password. */
	protected String password;
	/** List of managed connections. */
	protected Queue<Connection> connections;
	/** Number of total connections. */
	protected int connectionCount;
	
	/**
	 * Creates a new connection pool with a set amount of managed connections.
	 * @param connector	the connector to use.
	 * @param conns		the number of connections to pool.
	 * @param userName	the account user name.
	 * @param password	the account password.
	 */
	public SQLConnectionPool(SQLConnector connector, int conns, String userName, String password) throws SQLException
	{
		utilRef = connector;
		this.userName = userName;
		this.password = password;
		connectionCount = conns;
		connections = new Queue<Connection>();
		for (int i = 0; i < conns; i++)
			connections.enqueue(new PoolConnection(connector.getConnection(userName,password)));
	}
	
	/**
	 * Creates a new connection pool with a set amount of managed connections,
	 * and no credentials (used with databases that require no login).
	 * @param util		the DB utility library to use.
	 * @param conns		the number of connections to pool.
	 */
	public SQLConnectionPool(SQLConnector util, int conns) throws SQLException
	{
		utilRef = util;
		connectionCount = conns;
		connections = new Queue<Connection>();
		for (int i = 0; i < conns; i++)
			connections.enqueue(new PoolConnection(util.getConnection()));
	}
	
	/**
	 * Retrieves an available connection from the pool.
	 * @throws InterruptedException	if an interrupt is thrown by the current thread waiting for an available connection. 
	 */
	public Connection getAvailableConnection() throws InterruptedException
	{
		Connection c = null;
		synchronized (connections)
		{
			while (connections.isEmpty())
				wait();

			while (c == null)
			{
				try{
					Connection x = connections.dequeue();
					if (x.isClosed())
					{
						if (userName != null)
							connections.enqueue(x = new PoolConnection(utilRef.getConnection(userName,password)));
						else
							connections.enqueue(x = new PoolConnection(utilRef.getConnection()));
					}
					else
						c = x;
				} catch (SQLException e) {
					throw new RuntimeException("Could not make connection: "+e.getLocalizedMessage());
				}
			}
			
		}
		
		return c;
	}
	
	/**
	 * Gets the number of available connections.
	 */
	public int getAvailableConnectionCount()
	{
		return connections.size();
	}
	
	/**
	 * Gets the number of connections in use.
	 */
	public int getUsedConnectionCount()
	{
		return connectionCount - getAvailableConnectionCount();
	}

	/**
	 * Gets the number of total connections.
	 */
	public int getTotalConnectionCount()
	{
		return connectionCount;
	}

	/**
	 * Generates a transaction for multiple queries in one set.
	 * This transaction performs all of its queries through one connection.
	 * The connection is held by this transaction until it is finished via {@link SQLTransaction#complete()}.
	 * @param transactionLevel the isolation level of the transaction.
	 * @return a {@link SQLTransaction} object to handle a contiguous transaction.
	 * @throws InterruptedException	if an interrupt is thrown by the current thread waiting for an available connection. 
	 * @since 2.3.0
	 */
	public SQLTransaction startTransaction(Level transactionLevel) throws InterruptedException
	{
		return new SQLTransaction(getAvailableConnection(), transactionLevel);
	}

	/**
	 * Releases a connection back to the pool.
	 */
	public void releaseConnection(Connection c)
	{
		synchronized (connections)
		{
			connections.enqueue(c);
			connections.notifyAll();
		}
	}
	
	/**
	 * PoolConnection class.
	 */
	protected class PoolConnection extends SQLConnectionAdapter
	{		
		PoolConnection(Connection conn)
		{
			super(conn);
		}
		
		@Override
		public void close() throws SQLException
		{
			releaseConnection(this);
		}
		
	}
	
}
