/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis HyperlogLog management.
 * @author Matthew Tropiano
 */
public interface RedisHyperlogCommands
{
	/**
	 * <p>From <a href="http://redis.io/commands/pfadd">http://redis.io/commands/pfadd</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) to add every element.</p>
	 * <p>Adds all the element arguments to the HyperLogLog data structure stored at the variable name specified as first argument.</p>
	 * @return true if at least one internal register was altered, false otherwise.
	 */
	public boolean pfadd(String key, String element, String... elements);

	/**
	 * <p>From <a href="http://redis.io/commands/pfcount">http://redis.io/commands/pfcount</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>Returns the approximated cardinality computed by the HyperLogLog data structure stored at the specified variable, which is 0 if the variable does not exist.</p>
	 * @return the number of unique elements observed via {@link #pfadd(String, String, String...)}.
	 */
	public long pfcount(String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/pfmerge">http://redis.io/commands/pfmerge</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) to merge N HyperLogLogs, but with high constant times.</p>
	 * <p>Merge multiple HyperLogLog values into an unique value that will approximate the cardinality of the union of the observed Sets of the source HyperLogLog structures.</p>
	 * @return always true.
	 */
	public boolean pfmerge(String destkey, String sourcekey, String... sourcekeys);

}
