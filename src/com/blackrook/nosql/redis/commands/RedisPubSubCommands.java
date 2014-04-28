/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis commands related to Pub/Sub messaging.
 * @author Matthew Tropiano
 */
public interface RedisPubSubCommands
{

	/**
	 * <p>From <a href="http://redis.io/commands/psubscribe">http://redis.io/commands/psubscribe</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of 
	 * patterns the client is already subscribed to.</p>
	 * <p>Subscribes the client to the given patterns.</p>
	 */
	public void psubscribe(String... patterns);

	/**
	 * <p>From <a href="http://redis.io/commands/punsubscribe">http://redis.io/commands/punsubscribe</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N+M) where N is the number of patterns 
	 * the client is already subscribed and M is the number of total patterns 
	 * subscribed in the system (by any client).</p>
	 * <p>Unsubscribes the client from the given patterns, or from all of them if none is given.</p>
	 */
	public void punsubscribe(String... patterns);

	/**
	 * <p>From <a href="http://redis.io/commands/subscribe">http://redis.io/commands/subscribe</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of channels to subscribe to.</p>
	 * <p>Subscribes the client to the specified channels.</p>
	 */
	public void subscribe(String... channels);

	/**
	 * <p>From <a href="http://redis.io/commands/unsubscribe">http://redis.io/commands/unsubscribe</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of clients already subscribed to a channel.</p>
	 * <p>Unsubscribes the client from the given channels, or from all of them if none is given.</p>
	 */
	public void unsubscribe(String... channels);
	
}
