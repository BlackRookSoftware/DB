package com.blackrook.nosql.redis;

import java.io.IOException;
import java.net.UnknownHostException;

import com.blackrook.commons.hash.Hash;
import com.blackrook.commons.linkedlist.Queue;

/**
 * A connection pool for Redis socket connections because screw using
 * JedisPool and its dumb Apache Pool dependencies.
 * @author Matthew Tropiano
 */
public class RedisConnectionPool
{
	/** Subscription counter. */
	private static int SUBSCRIPTION_COUNTER = 0;
	/** Subscription counter mutex. */
	private static Integer SUBSCRIPTION_COUNTER_MUTEX = new Integer(0);

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
		this();
		info = new RedisInfo(host, port, password);
		//info.setTimeout(0);
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

	/**
	 * A thread spawned for subscriptions.
	public class SubcriptionThread extends Thread
	{
		private JedisPubSub pubsub;
		private String[] initChannels;
		
		private SubcriptionThread(JedisPubSub pubsub, String[] initChannels)
		{
			int t;
			this.pubsub = pubsub;
			this.initChannels = initChannels;

			synchronized (SUBSCRIPTION_COUNTER_MUTEX)
			{
				t = (++SUBSCRIPTION_COUNTER);
			}
			setName("RedisSubscriber-"+t);
			setDaemon(true);
		}
		
		@Override
		public void run()
		{
			(new Jedis(info)).subscribe(pubsub, initChannels);
		}
		
	}
	 */

}
