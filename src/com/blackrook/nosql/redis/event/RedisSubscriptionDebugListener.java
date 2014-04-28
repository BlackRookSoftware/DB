/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.event;

import java.io.PrintStream;

/**
 * An adapter class for {@link RedisSubscriptionListener} that prints messages to a {@link PrintStream}
 * when events are heard.
 * @author Matthew Tropiano
 */
public class RedisSubscriptionDebugListener implements RedisSubscriptionListener
{
	/** Output stream for messages. */
	private PrintStream out;
	
	/**
	 * Creates a listener that dumps to {@link System#out}.
	 */
	public RedisSubscriptionDebugListener()
	{
		this(System.out);
	}
	
	/**
	 * Creates a listener.
	 * @param out the {@link PrintStream} to dump output to.
	 */
	public RedisSubscriptionDebugListener(PrintStream out)
	{
		this.out = out;
	}
	
	@Override
	public void onSubscribe(String channelName, long channelTotal)
	{
		out.printf("[%s] SUBSCRIBED TO %s, %d total.\n", Thread.currentThread().getName(), channelName, channelTotal);
	}

	@Override
	public void onUnsubscribe(String channelName, long channelTotal)
	{
		out.printf("[%s] UNSUBSCRIBED FROM %s, %d total.\n", Thread.currentThread().getName(), channelName, channelTotal);
	}

	@Override
	public void onPatternSubscribe(String channelPattern, long channelTotal)
	{
		out.printf("[%s] SUBSCRIBED TO PATTERN %s, %d total.\n", Thread.currentThread().getName(), channelPattern, channelTotal);
	}

	@Override
	public void onPatternUnsubscribe(String channelPattern, long channelTotal)
	{
		out.printf("[%s] UNSUBSCRIBED FROM PATTERN %s, %d total.\n", Thread.currentThread().getName(), channelPattern, channelTotal);
	}

	@Override
	public void onMessageReceive(String channel, String message)
	{
		out.printf("[%s] %s: %s\n", Thread.currentThread().getName(), channel, message);
	}

	@Override
	public void onPatternMessageReceive(String channelPattern, String channel, String message)
	{
		out.printf("[%s] MATCHED %s; %s: %s\n", Thread.currentThread().getName(), channelPattern, channel, message);
	}

}
