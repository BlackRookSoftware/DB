/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis;

import java.io.IOException;
import java.net.UnknownHostException;

import com.blackrook.commons.Counter;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.util.ArrayUtils;
import com.blackrook.commons.util.ThreadUtils;
import com.blackrook.commons.util.ValueUtils;
import com.blackrook.nosql.redis.commands.RedisPubSubCommands;
import com.blackrook.nosql.redis.event.RedisSubscriptionListener;
import com.blackrook.nosql.redis.exception.RedisException;

/**
 * A special Redis connection that is essentially a subscription to
 * one or more Redis channels. Once a connection is opened, this
 * connection will listen indefinitely until it is closed or is
 * unsubscribed from all channels that it is listening to.
 * <p>
 * This will fire events to {@link RedisSubscriptionListener}s that have
 * been attached to this connection. The thread that is spawned by this
 * connection can be set to either be daemon or not (see {@link Thread#setDaemon(boolean)}) so that
 * its life does or does not affect JVM runtime life.
 * @author Matthew Tropiano
 */
public class RedisPubSubConnection extends RedisConnectionAbstract implements RedisPubSubCommands
{
	/* Command fragments. */
	private static final String[] COMMAND_SUBSCRIBE = new String[]{"SUBSCRIBE"};
	private static final String[] COMMAND_UNSUBSCRIBE = new String[]{"UNSUBSCRIBE"};
	private static final String[] COMMAND_PSUBSCRIBE = new String[]{"PSUBSCRIBE"};
	private static final String[] COMMAND_PUNSUBSCRIBE = new String[]{"PUNSUBSCRIBE"};
	
	/** List of subscription listeners. */
	private Queue<RedisSubscriptionListener> listeners;

	/** Thread counter for ids. */
	private Counter counter;
	/** Subscription listener thread. */
	private SubcriptionThread subcriptionThread;
	
	/**
	 * Creates an open connection to localhost, port 6379, the default Redis port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisPubSubConnection(RedisSubscriptionListener... listeners) throws IOException
	{
		super();
		construct(listeners);
	}

	/**
	 * Creates an open connection.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisPubSubConnection(String host, int port, RedisSubscriptionListener... listeners) throws IOException 
	{
		super(host, port);
		construct(listeners);
	}

	/**
	 * Creates an open connection.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 * @throws RedisException if the password in the server information is incorrect. 
	 */
	public RedisPubSubConnection(String host, int port, String password, RedisSubscriptionListener... listeners) throws IOException
	{
		super(host, port, password);
		construct(listeners);
	}

	/**
	 * Creates an open connection.
	 * @param info the {@link RedisInfo} class detailing a connection.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 * @throws RedisException if the password in the server information is incorrect. 
	 */
	public RedisPubSubConnection(RedisInfo info, RedisSubscriptionListener... listeners) throws IOException
	{
		super(info);
		construct(listeners);
	}

	// Finishes the constructor.
	private void construct(RedisSubscriptionListener... listeners)
	{
		this.listeners = new Queue<RedisSubscriptionListener>();
		this.counter = new Counter();
		addListener(listeners);
		(this.subcriptionThread = new SubcriptionThread()).start();
		while (!subcriptionThread.isAlive()) ThreadUtils.sleep(0, 250000);
	}
	
	/**
	 * Adds {@link RedisSubscriptionListener}s to this connection.
	 * All listeners on this connection are alerted when a message is received.
	 * @param listeners the listeners to add.
	 */
	public void addListener(RedisSubscriptionListener... listeners)
	{
		for (RedisSubscriptionListener listener : listeners)
			this.listeners.enqueue(listener);
	}

	/**
	 * Removes {@link RedisSubscriptionListener}s from this connection.
	 * @param listeners to remove.
	 */
	public void removeListeners(RedisSubscriptionListener... listeners)
	{
		for (RedisSubscriptionListener listener : listeners)
			this.listeners.remove(listener);
	}

	@Override
	public void subscribe(String... channels)
	{
		writer.writeArray(ArrayUtils.joinArrays(COMMAND_SUBSCRIBE, channels));
	}

	@Override
	public void unsubscribe(String... channels)
	{
		writer.writeArray(ArrayUtils.joinArrays(COMMAND_UNSUBSCRIBE, channels));
	}

	@Override
	public void psubscribe(String... patterns)
	{
		writer.writeArray(ArrayUtils.joinArrays(COMMAND_PSUBSCRIBE, patterns));
	}

	@Override
	public void punsubscribe(String... patterns) 
	{
		writer.writeArray(ArrayUtils.joinArrays(COMMAND_PUNSUBSCRIBE, patterns));
	}
	
	/**
	 * Fires an event to listeners when this subscription connection subscribes to a channel.
	 * @param channelName the subscribed channel.
	 * @param channelTotal the total number of channels that this subscriber is subscribed to.
	 */
	protected void fireOnSubscribe(String channelName, long channelTotal)
	{
		for (RedisSubscriptionListener listener : listeners)
			listener.onSubscribe(channelName, channelTotal);
	}

	/**
	 * Fires an event to listeners when this subscription connection unsubscribes from a channel.
	 * @param channelName the channel that this unsubscribed from.
	 * @param channelTotal the total number of channels that this subscriber is subscribed to.
	 */
	protected void fireOnUnsubscribe(String channelName, long channelTotal)
	{
		for (RedisSubscriptionListener listener : listeners)
			listener.onUnsubscribe(channelName, channelTotal);
	}

	/**
	 * Fires an event to listeners when this subscription connection subscribes to a set of channels via a pattern.
	 * @param channelPattern the channel pattern that this subscribed to.
	 * @param channelTotal the total number of channels that this subscriber is subscribed to.
	 */
	protected void fireOnPatternSubscribe(String channelPattern, long channelTotal)
	{
		for (RedisSubscriptionListener listener : listeners)
			listener.onPatternSubscribe(channelPattern, channelTotal);
	}

	/**
	 * Fires an event to listeners when this subscription connection unsubscribes from a set of channels via a pattern.
	 * @param channelPattern the channel pattern that this unsubscribed from.
	 * @param channelTotal the total number of channels that this subscriber is subscribed to.
	 */
	protected void fireOnPatternUnsubscribe(String channelPattern, long channelTotal)
	{
		for (RedisSubscriptionListener listener : listeners)
			listener.onPatternUnsubscribe(channelPattern, channelTotal);
	}

	/**
	 * Fires an event to listeners when this subscription connection receives a message from a channel.
	 * @param channel the channel that the message came from.
	 * @param message the message received.
	 */
	protected void fireOnMessageReceive(String channel, String message)
	{
		for (RedisSubscriptionListener listener : listeners)
			listener.onMessageReceive(channel, message);
	}

	/**
	 * Fires an event to listeners when this subscription connection receives a message from a channel.
	 * @param channelPattern the channel pattern that was matched.
	 * @param channel the channel that the message came from.
	 * @param message the message received.
	 */
	protected void fireOnPatternMessageReceive(String channelPattern, String channel, String message)
	{
		for (RedisSubscriptionListener listener : listeners)
			listener.onPatternMessageReceive(channelPattern, channel, message);
	}

	/**
	 * A thread spawned for subscriptions.
	 */
	private class SubcriptionThread extends Thread
	{
		SubcriptionThread()
		{
			setName("RedisSubscription-"+counter.incr());
			setDaemon(true);
		}
		
		@Override
		public void run()
		{
			String[] response = null;
			while (isConnected() && (response = reader.readArray()) != null)
			{
				if (response.length == 0)
					return;
				
				if (response[0].equals("subscribe"))
					fireOnSubscribe(response[1], ValueUtils.parseLong(response[2]));
				else if (response[0].equals("unsubscribe"))
					fireOnUnsubscribe(response[1], ValueUtils.parseLong(response[2]));
				else if (response[0].equals("psubscribe"))
					fireOnPatternSubscribe(response[1], ValueUtils.parseLong(response[2]));
				else if (response[0].equals("punsubscribe"))
					fireOnPatternUnsubscribe(response[1], ValueUtils.parseLong(response[2]));
				else if (response[0].equals("message"))
					fireOnMessageReceive(response[1], response[2]);
				else if (response[0].equals("pmessage"))
					fireOnPatternMessageReceive(response[1], response[2], response[3]);
			}
		}
		
	}

}
