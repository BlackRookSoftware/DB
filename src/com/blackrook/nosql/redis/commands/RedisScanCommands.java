/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.commands;

import com.blackrook.nosql.redis.data.RedisCursor;

/**
 * Command interface for key/value iteration in Redis.
 * @author Matthew Tropiano
 */
public interface RedisScanCommands
{

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0.
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @param cursor the cursor value.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor scan(long cursor);

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0.
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor scan(long cursor, String pattern);

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0.
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @param cursor the cursor value.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor scan(long cursor, long count);

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0.
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor scan(long cursor, String pattern, Long count);

	/**
	 * <p>From <a href="http://redis.io/commands/sscan">http://redis.io/commands/sscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor hscan(String key, long cursor);

	/**
	 * <p>From <a href="http://redis.io/commands/sscan">http://redis.io/commands/sscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor hscan(String key, long cursor, String pattern);

	/**
	 * <p>From <a href="http://redis.io/commands/sscan">http://redis.io/commands/sscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor hscan(String key, long cursor, long count);

	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor hscan(String key, long cursor, String pattern, Long count);

	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the set to scan.
	 * @param cursor the cursor value.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor sscan(String key, long cursor);

	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the set to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor sscan(String key, long cursor, String pattern);

	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the set to scan.
	 * @param cursor the cursor value.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor sscan(String key, long cursor, long count);

	/**
	 * <p>From <a href="http://redis.io/commands/sscan">http://redis.io/commands/sscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the set to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor sscan(String key, String cursor, String pattern, Long count);

	/**
	 * <p>From <a href="http://redis.io/commands/zscan">http://redis.io/commands/zscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, 
	 * including enough command calls for the cursor to return back to 0. N is the number of 
	 * elements inside the collection..</p>
	 * @param key the key of the sorted set to scan.
	 * @param cursor the cursor value.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor zscan(String key, long cursor);

	/**
	 * <p>From <a href="http://redis.io/commands/zscan">http://redis.io/commands/zscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, 
	 * including enough command calls for the cursor to return back to 0. N is the number of 
	 * elements inside the collection..</p>
	 * @param key the key of the sorted set to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor zscan(String key, long cursor, String pattern);

	/**
	 * <p>From <a href="http://redis.io/commands/zscan">http://redis.io/commands/zscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, 
	 * including enough command calls for the cursor to return back to 0. N is the number of 
	 * elements inside the collection..</p>
	 * @param key the key of the sorted set to scan.
	 * @param cursor the cursor value.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor zscan(String key, long cursor, long count);

	/**
	 * <p>From <a href="http://redis.io/commands/zscan">http://redis.io/commands/zscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, 
	 * including enough command calls for the cursor to return back to 0. N is the number of 
	 * elements inside the collection..</p>
	 * @param key the key of the sorted set to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor zscan(String key, long cursor, String pattern, Long count);
	
}
